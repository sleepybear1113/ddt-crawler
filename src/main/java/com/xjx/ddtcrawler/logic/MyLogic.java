package com.xjx.ddtcrawler.logic;

import com.xjx.ddtcrawler.domain.Item;
import com.xjx.ddtcrawler.domain.QueryUrl;
import com.xjx.ddtcrawler.domain.Result;
import com.xjx.ddtcrawler.domain.Template;
import com.xjx.ddtcrawler.http.HttpHelper;
import com.xjx.ddtcrawler.http.HttpResponseHelper;
import com.xjx.ddtcrawler.service.ItemService;
import com.xjx.ddtcrawler.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Result getRawResponseBody(QueryUrl queryUrl) {
        String url = queryUrl.buildUrl();
        if (url == null) {
            return null;
        }
        HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
        log.info(url);
        httpHelper.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3012.0 Safari/537.36");
        HttpResponseHelper request = httpHelper.request();
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

    public void processResultItem(Result result) {
        if (result == null) {
            return;
        }
        if (!result.isSuccess()) {
            return;
        }

        List<Item> items = result.getItem();
        if (CollectionUtils.isEmpty(items)) {
            return;
        }

        for (Item item : items) {
            item.buildSelf();
        }
    }

    public Result getItemsWithFillItemInfo(QueryUrl queryUrl, boolean insert) {
        Result result = getRawResponseBody(queryUrl);
        processResultItem(result);

        if (result == null) {
            return null;
        }

        if (!result.isSuccess()) {
            log.info("result is not success");
            return null;
        }

        List<Item> items = result.getItem();
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
