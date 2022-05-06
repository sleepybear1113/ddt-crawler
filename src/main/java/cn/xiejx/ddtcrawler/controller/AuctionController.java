package cn.xiejx.ddtcrawler.controller;

import cn.xiejx.cacher.Cacher;
import cn.xiejx.ddtcrawler.cookie.WebUser;
import cn.xiejx.ddtcrawler.domain.QueryUrl;
import cn.xiejx.ddtcrawler.domain.Result;
import cn.xiejx.ddtcrawler.domain.constant.AuctionConstant;
import cn.xiejx.ddtcrawler.exception.MyException;
import cn.xiejx.ddtcrawler.logic.AuctionLogic;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XJX
 * @date 2021/8/1 21:15
 */
@RestController
@Slf4j
public class AuctionController {
    @Resource
    private AuctionLogic auctionLogic;

    @Resource
    private Cacher<String, String> cacher;

    @RequestMapping("/auction/getAuctionItems")
    public Result getItems(@RequestParam(required = false, defaultValue = "1") Integer page,
                           @RequestParam(required = false, defaultValue = "-1") Long userId,
                           @RequestParam(required = false, defaultValue = "-1") Long buyerId,
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
        queryUrl.setBuyId(buyerId);
        queryUrl.setSort(sort);
        queryUrl.setPage(page);
        queryUrl.setName(itemName.trim());
        if (webUser.notAdmin()) {
            queryUrl.setUserId(-1L);
            queryUrl.setBuyId(-1L);
        }

        Result result = auctionLogic.getSingleResult(queryUrl);
        if (webUser.notAdmin()) {
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

        Result result = auctionLogic.getResultsByBatchPages(queryUrl, QueryUrl.DEFAULT_PAGES, true, 250L, priceType, sort, null);
        if (webUser.notAdmin()) {
            result.hideSensitiveInfo();
        }
        return result;
    }

    @RequestMapping("/www")
    public void a() {
        log.info(cacher.getScheduleName());
    }
}
