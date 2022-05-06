package cn.xiejx.ddtcrawler.task;

import cn.xiejx.cacher.Cacher;
import cn.xiejx.ddtcrawler.config.Ioc;
import cn.xiejx.ddtcrawler.cookie.WebUser;
import cn.xiejx.ddtcrawler.domain.Item;
import cn.xiejx.ddtcrawler.domain.QueryUrl;
import cn.xiejx.ddtcrawler.domain.Result;
import cn.xiejx.ddtcrawler.domain.constant.AuctionConstant;
import cn.xiejx.ddtcrawler.exception.MyException;
import cn.xiejx.ddtcrawler.logic.AuctionLogic;
import cn.xiejx.ddtcrawler.service.ItemService;
import cn.xiejx.ddtcrawler.utils.ThreadPoolUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author XJX
 * @date 2021/8/7 16:14
 */
@Component
@Slf4j
public class ItemTask {
    private static Long latestAuctionId = 0L;
    public static final String ITEM_TASK_KEY = "item_task_key";
    public static final String ITEM_TASK_LAST_AUCTION_TIME = "item_task_last_auction_time";

    @Resource
    private AuctionLogic auctionLogic;
    @Resource
    private ItemService itemService;

    @Qualifier(Ioc.COMMON_CACHER_NAME)
    @Resource
    private Cacher<String, Object> cacher;

    public void initAuctionId() {
        long maxAuctionId = itemService.getMaxAuctionId();
        if (maxAuctionId > 0) {
            latestAuctionId = maxAuctionId;
        }
        log.info("当前数据库最大的 auctionId = " + maxAuctionId);
    }

    public Long getLatestAuctionId(WebUser webUser) throws MyException {
        QueryUrl queryUrl = new QueryUrl();
        queryUrl.setOrder(AuctionConstant.OrderEnum.TIME.getValue());
        queryUrl.setSort(true);
        queryUrl.setWebUser(webUser);

        Result result = auctionLogic.getItemsWithFillItemInfo(queryUrl, false);
        if (result == null) {
            return null;
        }

        boolean success = result.isSuccess();
        if (!success) {
            return null;
        }

        List<Item> items = result.getItems();
        if (CollectionUtils.isEmpty(items)) {
            return null;
        }

        items.removeIf(item -> item.getAuctionId() <= latestAuctionId);
        if (CollectionUtils.isEmpty(items)) {
            // 没有最新的了，那就是当前的 auctionId
            log.info("当前没有获取到更多 auctions");
            return latestAuctionId;
        }

        Collections.reverse(items);

        Long maxAuctionId = 0L;
        for (Item item : items) {
            Long auctionId = item.getAuctionId();
            if (auctionId > maxAuctionId) {
                maxAuctionId = auctionId;
            }
        }

        itemService.insert(items);
        log.info("插入数据库条数：" + items.size());

        return maxAuctionId;
    }

    public String startTask(WebUser webUser) throws MyException {
        if (webUser == null) {
            throw new MyException("user 为空");
        }

        Object cache = cacher.get(ITEM_TASK_KEY);
        if (cache != null) {
            return "running";
        }

        initAuctionId();

        Runnable runnable = () -> {
            long delay;
            int count = 1;
            do {
                // 15 分钟锁
                cacher.set(ITEM_TASK_KEY, 1, 1000 * 60 * 15L);
                log.info("获取次数：" + count++);
                delay = singleTask(webUser);
                sleep(delay);
            } while (delay > 0);
            cacher.remove(ITEM_TASK_KEY);
            log.info("缓存 key 被删除");
        };
        ThreadPoolUtil.start(runnable);

        return "start";
    }

    public long singleTask(WebUser webUser) {
        long nextDelayTime;
        int maxRetryTimes = 3;
        Long auctionId = null;

        for (int i = 0; i < maxRetryTimes; i++) {
            Object cache = cacher.get(ITEM_TASK_KEY);
            if (cache == null) {
                log.info("锁被释放，停止获取");
                return -1L;
            }

            // 获取拍卖场并且写入数据库，返回最大的 id
            try {
                auctionId = getLatestAuctionId(webUser);
            } catch (Exception e) {
                log.error("获取拍卖场数据失败", e);
            }
            if (auctionId != null) {
                break;
            }
            long t = 1000 * 5L;
            log.info("获取失败，重新获取");
            sleep(t);
        }

        if (auctionId == null) {
            log.info("获取失败，结束");
            cacher.remove(ITEM_TASK_KEY);
            return -1L;
        }
        long now = System.currentTimeMillis();

        // 上一次的时间
        Long lastAuctionTime = (Long) cacher.get(ITEM_TASK_LAST_AUCTION_TIME);
        // 两次 auction 差值
        long subAuction = auctionId - latestAuctionId;
        log.info("与上次记录的差值：" + subAuction);
        // 不是第一次获取
        if (lastAuctionTime != null) {
            // 两次获取之间的间隔
            long subTime = now - lastAuctionTime;

            latestAuctionId = auctionId;
            // 获取下次的时延
            nextDelayTime = getDelayTime(subTime, subAuction);
            log.info("下次获取时间：" + nextDelayTime);
        } else {
            nextDelayTime = getDelayTime(120 * 1000, subAuction);
            log.info("这是第一次获取，没有上一次获取的时候");
        }

        cacher.set(ITEM_TASK_LAST_AUCTION_TIME, now);

        return nextDelayTime;
    }

    public long getDelayTime(long subTime, long subAuction) {
        long pageSize = 16;
        long minTime = 1000 * 20;
        long maxTime = 1000 * 300;
        if (subAuction == 0) {
            return maxTime;
        }
        double subPage = subAuction * 1.0 / pageSize;

        long time = (long) (subTime / subPage);
        if (time < minTime) {
            time = minTime;
        } else if (time > maxTime) {
            time = maxTime;
        }

        return time;
    }

    public void sleep(long time) {
        log.info("开始 sleep：" + time / 1000);
        long times = time / 100;
        try {
            int count = 0;
            for (int i = 0; i < times; i++) {
                Thread.sleep(100);
                if (count++ % 100 == 0) {
                    // 10s 打印一次
                    log.info("sleep：" + count / 10);
                }
            }
            Thread.sleep(time % 100);
            log.info("sleep end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
