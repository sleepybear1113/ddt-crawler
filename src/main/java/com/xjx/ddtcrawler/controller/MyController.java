package com.xjx.ddtcrawler.controller;

import com.xjx.ddtcrawler.cache.UserInfoCache;
import com.xjx.ddtcrawler.domain.*;
import com.xjx.ddtcrawler.domain.constant.AuctionConstant;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.logic.MyLogic;
import com.xjx.ddtcrawler.service.TemplateService;
import com.xjx.ddtcrawler.task.ItemTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author XieJiaxing
 * @date 2021/8/1 21:15
 */
@RestController
public class MyController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private MyLogic myLogic;
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private ItemTask itemTask;

    @RequestMapping("/saveUser")
    public Boolean saveUser(Long userId, String key) {
        userInfoCache.saveUser(userId, key);
        return true;
    }

    @RequestMapping("/getTemplateById")
    public Template getTemplateById(Long id) {
        return templateService.getById(id);
    }

    @RequestMapping("/getAuctionItems")
    public Result getItems(Long selfId,
                           @RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "-1") Long userId,
                           @RequestParam(required = false, defaultValue = "") String itemName,
                           @RequestParam(required = false, defaultValue = "2") Integer order,
                           @RequestParam(required = false, defaultValue = "true") Boolean sort) throws MyException, InterruptedException {
        boolean inRange = AuctionConstant.OrderEnum.isInRange(order);
        if (!inRange) {
            throw new MyException("入参有误：type = " + order);
        }

        if (page <= 0) {
            page = 1;
        }

        UserInfo userInfo = userInfoCache.getByUserId(selfId);
        if (userInfo == null) {
            throw new MyException("用户信息未保存");
        }

        QueryUrl queryUrl = new QueryUrl();
        queryUrl.setUserInfo(userInfo);
        queryUrl.setOrder(order);
        queryUrl.setUserId(userId);
        queryUrl.setSort(sort);
        queryUrl.setPage(page);
        queryUrl.setName(itemName.trim());

        return myLogic.getSingleResult(queryUrl);
    }

    @RequestMapping("/getAuctionPriceOder")
    public Result getAuctionPriceOder(Long selfId, String itemName,
                                      @RequestParam(required = false, defaultValue = "false") Boolean sort,
                                      @RequestParam(required = false, defaultValue = "true") Boolean priceType) throws MyException, InterruptedException {
        if (selfId == null || StringUtils.isBlank(itemName)) {
            throw new MyException("入参为空");
        }
        UserInfo userInfo = userInfoCache.getByUserId(selfId);
        if (userInfo == null) {
            throw new MyException("用户信息未保存");
        }

        QueryUrl queryUrl = new QueryUrl();
        queryUrl.setUserInfo(userInfo);
        queryUrl.setOrder(AuctionConstant.OrderEnum.PRICE.getValue());
        queryUrl.setUserId(-1L);
        queryUrl.setSort(sort);
        queryUrl.setName(itemName.trim());

        Result result = myLogic.getResultsByBatchPages(queryUrl, QueryUrl.DEFAULT_PAGES, true, 250L);
        List<Item> items = result.getItems();

        if (priceType) {
            items.sort(Comparator.comparing(Item::getUnitMouthfulPrice));
        } else {
            items.sort(Comparator.comparing(Item::getUnitPrice));
        }
        if (sort) {
            Collections.reverse(items);
        }

        return result;
    }

    @RequestMapping("/startAuction")
    public String startAuction(Long userId) {
        return itemTask.startTask(userId);
    }

    @RequestMapping("/saveTemplate")
    public Boolean saveTemplate(Long templateId, String templateName) {
        Template template = new Template();
        template.setName(templateName);
        template.setId(templateId);
        return templateService.save(template);
    }
}
