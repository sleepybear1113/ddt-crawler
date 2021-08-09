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

        return myLogic.getResultsByBatchPages(queryUrl);
    }

    @RequestMapping("/getAuctionPriceOder")
    public Result getAuctionPriceOder(Long selfId, String itemName,
                                      @RequestParam(required = false, defaultValue = "false") Boolean sort,
                                      @RequestParam(required = false, defaultValue = "true") Boolean priceType) throws MyException {
        if (selfId == null || StringUtils.isBlank(itemName)) {
            throw new MyException("入参为空");
        }
        UserInfo userInfo = userInfoCache.getByUserId(selfId);
        if (userInfo == null) {
            throw new MyException("用户信息未保存");
        }
        String key = userInfo.getKey();
        if (StringUtils.isBlank(key)) {
            throw new MyException("用户的 key 不存在");
        }

        QueryUrl queryUrl = new QueryUrl();
        queryUrl.setKey(key);
        queryUrl.setSelfId(selfId);
        queryUrl.setOrder(AuctionConstant.OrderEnum.PRICE.getValue());
        queryUrl.setUserId(-1L);
        queryUrl.setSort(sort);
        queryUrl.setName(itemName.trim());

        Result r = new Result();
        List<Item> items = new ArrayList<>();
        r.setItems(items);
        int maxPage = 10;
        for (int i = 1; i <= maxPage; i++) {
            queryUrl.setPage(i);
            Result result = myLogic.getItemsWithFillItemInfo(queryUrl, false);
            if (!result.isSuccess()) {
                break;
            }
            Long total = result.getTotal();
            if (total == null) {
                break;
            }
            r.setTotal(total);
            r.setMessage(result.getMessage());
            int ceil = (int) Math.ceil(total * 1.0 / 20) + 1;
            if (i > ceil) {
                break;
            }

            items.addAll(result.getItems());

            try {
                Thread.sleep(333);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (priceType) {
            items.sort(Comparator.comparing(Item::getUnitMouthfulPrice));
        } else {
            items.sort(Comparator.comparing(Item::getUnitPrice));
        }
        if (sort) {
            Collections.reverse(items);
        }

        return r;
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
