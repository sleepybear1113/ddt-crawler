package com.xjx.ddtcrawler.domain;

import com.xjx.ddtcrawler.cookie.WebUser;
import com.xjx.ddtcrawler.domain.constant.AuctionConstant;
import com.xjx.ddtcrawler.exception.MyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author XieJiaxing
 * @date 2021/8/1 15:40
 */
@Data
@Slf4j
public class QueryUrl {
    public static final String URL = "https://s57_app1105673153_qqgame_com.7road.net/request/auctionpagelist.ashx";
    public static final Random RANDOM = new Random();
    public static final List<Integer> DEFAULT_PAGES = new ArrayList<>();
    static {
        DEFAULT_PAGES.add(1);
        DEFAULT_PAGES.add(2);
        DEFAULT_PAGES.add(3);
        DEFAULT_PAGES.add(4);
        DEFAULT_PAGES.add(5);
        DEFAULT_PAGES.add(6);
        DEFAULT_PAGES.add(7);
    }

    /**
     * 页码
     */
    private Integer page = 1;
    /**
     * 自己的 id
     */
    private Long selfId;
    /**
     * 商品类别
     */
    private Integer type = -1;
    /**
     * 拍卖场名，置空
     */
    private String auctions = "";
    /**
     * 买家 id
     */
    private Long buyId = -1L;
    /**
     * 自己的 key
     */
    private String key;
    /**
     * 商品名称
     */
    private String name = "";
    /**
     * 卖家 id
     */
    private Long userId = -1L;
    private Double rnd;
    private Long pay = -1L;
    /**
     * 排序类别<br/>
     * 见 {@link AuctionConstant.OrderEnum}
     */
    private Integer order = 0;
    /**
     * false 为升序，true 为降序
     */
    private Boolean sort = AuctionConstant.SortOrderEnum.DESC.getType();

    public void setWebUser(WebUser webUser) {
        if (webUser == null) {
            return;
        }

        this.key = webUser.getKey();
        this.selfId = webUser.getUserId();
    }

    /**
     * 构建请求 url
     *
     * @return url
     */
    public String buildUrl() throws MyException {
        if (page == null || order == null || sort == null || selfId == null) {
            throw new MyException("url 组装出错，请检查字段空值");
        }
        rnd = RANDOM.nextDouble();
        String format = "?page=%s&selfid=%s&type=%s&Auctions=%s&buyID=%s&key=%s&name=%s&userId=%s&rnd=%s&pay=%s&order=%s&sort=%s";

        String url = URL + String.format(format, page, selfId, type, auctions, buyId, key, ec(name), userId, rnd, pay, order, sort);
        log.info(url);
        return url;
    }

    /**
     * 一个封装的 url encode 方法
     *
     * @param s 入参
     * @return encode
     */
    private static String ec(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }

        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("url 编码错误", e);
            return "";
        }
    }
}
