package com.xjx.ddtcrawler.task;

import com.xjx.ddtcrawler.cache.CommonCache;
import com.xjx.ddtcrawler.cache.UserInfoCache;
import com.xjx.ddtcrawler.domain.Item;
import com.xjx.ddtcrawler.domain.QueryUrl;
import com.xjx.ddtcrawler.domain.Result;
import com.xjx.ddtcrawler.domain.UserInfo;
import com.xjx.ddtcrawler.domain.constant.AuctionConstant;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.logic.MyLogic;
import com.xjx.ddtcrawler.service.ItemService;
import com.xjx.ddtcrawler.utils.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author XieJiaxing
 * @date 2021/8/7 16:14
 */
@Component
@Slf4j
public class ItemTask {
    private static Long latestAuctionId = 0L;
    public static final String ITEM_TASK_KEY = "item_task_key";
    public static final String ITEM_TASK_LAST_AUCTION_TIME = "item_task_last_auction_time";

    @Autowired
    private MyLogic myLogic;
    @Autowired
    private ItemService itemService;
    @Autowired
    private CommonCache commonCache;
    @Autowired
    private UserInfoCache userInfoCache;

    public void initAuctionId() {
        long maxAuctionId = itemService.getMaxAuctionId();
        if (maxAuctionId > 0) {
            latestAuctionId = maxAuctionId;
        }
        log.info("当前数据库最大的 auctionId = " + maxAuctionId);
    }

    public Long getLatestAuctionId(UserInfo userInfo) throws MyException {
        QueryUrl queryUrl = new QueryUrl();
        queryUrl.setOrder(AuctionConstant.OrderEnum.TIME.getValue());
        queryUrl.setSort(true);
        queryUrl.setUserInfo(userInfo);

        Result result = myLogic.getItemsWithFillItemInfo(queryUrl, false);
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

    public String startTask(Long selfId) {
        if (selfId == null) {
            return "user 为空";
        }
        UserInfo userInfo = userInfoCache.getByUserId(selfId);
        if (userInfo == null) {
            return "user key 不存在";
        }

        Object cache = commonCache.getCache(ITEM_TASK_KEY);
        if (cache != null) {
            return "running";
        }

        initAuctionId();

        Runnable runnable = () -> {
            long delay;
            int count = 1;
            do {
                // 15 分钟锁
                commonCache.setCache(ITEM_TASK_KEY, 1, 1000 * 60 * 15L);
                log.info("获取次数：" + count++);
                delay = singleTask(userInfo);
                sleep(delay);
            } while (delay > 0);
            commonCache.delCache(ITEM_TASK_KEY);
            log.info("缓存 key 被删除");
        };
        ThreadPoolUtil.start(runnable);

        return "start";
    }

    public long singleTask(UserInfo userInfo) {
        long nextDelayTime;
        int maxRetryTimes = 3;
        Long auctionId = null;

        for (int i = 0; i < maxRetryTimes; i++) {
            Object cache = commonCache.getCache(ITEM_TASK_KEY);
            if (cache == null) {
                log.info("锁被释放，停止获取");
                return -1L;
            }

            // 获取拍卖场并且写入数据库，返回最大的 id
            try {
                auctionId = getLatestAuctionId(userInfo);
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
            commonCache.delCache(ITEM_TASK_KEY);
            return -1L;
        }
        long now = System.currentTimeMillis();

        // 上一次的时间
        Long lastAuctionTime = (Long) commonCache.getCache(ITEM_TASK_LAST_AUCTION_TIME);
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

        commonCache.setCache(ITEM_TASK_LAST_AUCTION_TIME, now);

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
