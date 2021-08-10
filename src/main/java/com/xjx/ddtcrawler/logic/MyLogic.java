package com.xjx.ddtcrawler.logic;

import com.xjx.ddtcrawler.domain.Item;
import com.xjx.ddtcrawler.domain.QueryUrl;
import com.xjx.ddtcrawler.domain.Result;
import com.xjx.ddtcrawler.domain.Template;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.http.HttpHelper;
import com.xjx.ddtcrawler.http.HttpResponseHelper;
import com.xjx.ddtcrawler.service.ItemService;
import com.xjx.ddtcrawler.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author XieJiaxing
 * @date 2021/8/1 21:24
 */
@Component
@Slf4j
public class MyLogic {

    @Autowired
    private TemplateService templateService;
    @Autowired
    private ItemService itemService;

    /**
     * 请求 url 获取 ResponseBody 后进行 Result 对象解析
     *
     * @param queryUrl queryUrl
     * @return Result
     */
    public Result getRawResponseBody(QueryUrl queryUrl) throws MyException {
        String url = queryUrl.buildUrl();
        if (url == null) {
            return null;
        }
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        log.trace(url);
        httpHelper.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3012.0 Safari/537.36");

        long t0 = System.currentTimeMillis();
        HttpResponseHelper request = httpHelper.request();
        long t1 = System.currentTimeMillis();
        log.trace("请求耗时：" + (t1 - t0) + " ms");
        String responseBody = request.getResponseBody();
        if (responseBody == null) {
            return null;
        }

        Result result = Result.parseResult(responseBody);
        if (result == null) {
            System.out.println("请求返回内容为空");
            return null;
        }
        return result;
    }

    /**
     * 获取单页拍卖场物品，按照拍卖场顺序获取并去重
     *
     * @param queryUrl queryUrl
     * @return Result
     * @throws MyException          Exception
     * @throws InterruptedException Exception
     */
    public Result getSingleResult(QueryUrl queryUrl) throws MyException, InterruptedException {
        List<Integer> pages = new ArrayList<>();
        pages.add(queryUrl.getPage());
        return getResultsByBatchPages(queryUrl, pages, true, 250L);
    }

    /**
     * 批量获取多页的拍卖场物品信息
     *
     * @param queryUrl queryUrl
     * @param pages    页码集合
     * @param sleep    时延，毫秒
     * @return Result
     * @throws MyException          exception
     * @throws InterruptedException exception
     */
    public Result getResultsByBatchPages(QueryUrl queryUrl, List<Integer> pages, boolean withTemplate, Long sleep) throws MyException, InterruptedException {
        if (CollectionUtils.isNotEmpty(pages)) {
            pages.removeIf(p -> p <= 0);
            // 去重
            pages = new ArrayList<>(new HashSet<>(pages));
            // 升序排序
            pages.sort(Integer::compareTo);
        }
        if (CollectionUtils.isEmpty(pages)) {
            // pages 为空默认获取第一页
            pages = new ArrayList<>();
            pages.add(1);
        }

        // 新建默认的返回总共
        Result totalResult = new Result();
        totalResult.setItems(new ArrayList<>());
        // 默认设置为失败
        totalResult.setValue(false);

        for (int i = 0; i < pages.size(); i++) {
            int page = pages.get(i);
            queryUrl.setPage(page);

            Result result = getRawResponseBody(queryUrl);
            if (!result.isSuccess()) {
                continue;
            }

            Long total = result.getTotal();
            int ceil = (int) Math.ceil(total * 1.0 / 20) + 1;
            // 判断是否获取的页数超过了最大页数
            if (i > ceil) {
                // 这里设置为 pages.size() 则保证下一步跳出循环，并且能走到别的逻辑
                i = pages.size();
            }

            // 每次成功后 total 累加，items 累加
            totalResult.getItems().addAll(result.getItems());
            totalResult.setValue(true);

            if (i + 1 < pages.size()) {
                // 每次请求之后的时延，防止高并发
                TimeUnit.MILLISECONDS.sleep(sleep);
            }
        }

        if (totalResult.isSuccess()) {
            // 根据 auctionId 去重。按照拍卖场获取的顺序，每次获取 auctionId 加入到 set 中防止 加入到 itemResult 的时候重复
            List<Item> items = totalResult.getItems();
            HashSet<Long> auctionSet = new HashSet<>();
            List<Item> itemResult = new ArrayList<>();

            for (Item item : items) {
                Long auctionId = item.getAuctionId();
                if (auctionId == null) {
                    continue;
                }

                if (auctionSet.contains(auctionId)) {
                    continue;
                }

                itemResult.add(item);
                auctionSet.add(auctionId);
            }

            totalResult.setItems(itemResult);
            totalResult.setTotal((long) itemResult.size());

            // 构建 items
            totalResult.parseItems();

            // 填充物品信息
            if (withTemplate) {
                fillItemTemplate(totalResult);
            }
        }

        return totalResult;
    }

    public void processResultItem(Result result) {
        if (result == null) {
            return;
        }
        if (!result.isSuccess()) {
            return;
        }

        List<Item> items = result.getItems();
        if (CollectionUtils.isEmpty(items)) {
            return;
        }

        for (Item item : items) {
            item.buildSelf();
        }
    }

    /**
     * 填充 result 里面的 items 物品名和价格
     *
     * @param result result
     */
    private void fillItemTemplate(Result result) {
        if (result == null || !result.isSuccess()) {
            log.warn("无法填充物品名，result 为空");
            return;
        }

        List<Item> items = result.getItems();
        Set<Long> templateSet = new HashSet<>();
        for (Item item : items) {
            Long templateId = item.getTemplateId();
            if (templateId == null) {
                continue;
            }

            templateSet.add(templateId);
        }

        List<Long> templateIds = new ArrayList<>(templateSet);
        Map<Long, Template> templateMap = templateService.getMapByIds(templateIds);
        log.info("找到对应 template 数量：" + templateMap.size());
        if (MapUtils.isEmpty(templateMap)) {
            return;
        }

        for (Item item : items) {
            Long templateId = item.getTemplateId();
            Template template = templateMap.get(templateId);
            if (template == null) {
                continue;
            }
            String templateName = template.getName();
            Double price = template.getPrice();
            item.setTemplateName(templateName);
            item.setUserDefinePrice(price);
        }
    }

    public Result getItemsWithFillItemInfo(QueryUrl queryUrl, boolean insert) throws MyException {
        Result result = getRawResponseBody(queryUrl);
        processResultItem(result);

        if (result == null) {
            return null;
        }

        if (!result.isSuccess()) {
            log.info("result is not success");
            return null;
        }

        List<Item> items = result.getItems();
        if (CollectionUtils.isEmpty(items)) {
            log.info("result items are empty");
            return null;
        }

        List<Long> templateIds = new ArrayList<>();
        for (Item item : items) {
            Long templateId = item.getTemplateId();
            if (templateId == null) {
                continue;
            }
            templateIds.add(templateId);
        }
        if (CollectionUtils.isEmpty(templateIds)) {
            return result;
        }

        Map<Long, Template> templateMap = templateService.getMapByIds(templateIds);
        log.info("找到对应 template 数量：" + templateMap.size());
        if (MapUtils.isEmpty(templateMap)) {
            return result;
        }

        for (Item item : items) {
            Long templateId = item.getTemplateId();
            Template template = templateMap.get(templateId);
            if (template == null) {
                continue;
            }
            String templateName = template.getName();
            Double price = template.getPrice();
            item.setTemplateName(templateName);
            item.setUserDefinePrice(price);
        }

        if (insert) {
            itemService.insert(items);
        }

        return result;
    }
}
