package com.xjx.ddtcrawler.controller;

import com.xjx.ddtcrawler.cookie.WebUser;
import com.xjx.ddtcrawler.domain.QueryUrl;
import com.xjx.ddtcrawler.domain.Result;
import com.xjx.ddtcrawler.domain.constant.AuctionConstant;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.logic.AuctionLogic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XieJiaxing
 * @date 2021/8/1 21:15
 */
@RestController
public class AuctionController {
    @Autowired
    private AuctionLogic auctionLogic;

    @RequestMapping("/auction/getAuctionItems")
    public Result getItems(@RequestParam(required = false, defaultValue = "1") Integer page,
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

        WebUser webUser = WebUser.getSafeWebUser();
        boolean temporaryUser = webUser.isTemporaryUser();
        QueryUrl queryUrl = new QueryUrl();
        queryUrl.setWebUser(webUser);
        queryUrl.setOrder(order);
        queryUrl.setUserId(userId);
        queryUrl.setSort(sort);
        queryUrl.setPage(page);
        queryUrl.setName(itemName.trim());
        if (temporaryUser) {
            queryUrl.setUserId(-1L);
        }

        Result result = auctionLogic.getSingleResult(queryUrl);
        if (temporaryUser) {
            result.hideSensitiveInfo();
        }
        return result;
    }

    @RequestMapping("/auction/getAuctionPriceOder")
    public Result getAuctionPriceOder(String itemName,
                                      @RequestParam(required = false, defaultValue = "false") Boolean sort,
                                      @RequestParam(required = false, defaultValue = "0") Integer priceType) throws MyException, InterruptedException {
        if (StringUtils.isBlank(itemName)) {
            throw new MyException("入参为空");
        }

        WebUser webUser = WebUser.getSafeWebUser();
        QueryUrl queryUrl = new QueryUrl();
        queryUrl.setWebUser(webUser);
        queryUrl.setOrder(AuctionConstant.OrderEnum.PRICE.getValue());
        queryUrl.setUserId(-1L);
        queryUrl.setSort(sort);
        queryUrl.setName(itemName.trim());

        Result result = auctionLogic.getResultsByBatchPages(queryUrl, QueryUrl.DEFAULT_PAGES, true, 250L, priceType, sort);
        if (webUser.isTemporaryUser()) {
            result.hideSensitiveInfo();
        }
        return result;
    }
}
