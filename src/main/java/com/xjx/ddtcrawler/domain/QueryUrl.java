package com.xjx.ddtcrawler.domain;

import com.xjx.ddtcrawler.domain.constant.AuctionConstant;
import lombok.Data;

import java.util.Random;

/**
 * @author XieJiaxing
 * @date 2021/8/1 15:40
 */
@Data
public class QueryUrl {
    public static final String URL = "https://s57_app1105673153_qqgame_com.7road.net/request/auctionpagelist.ashx";

    private Integer page = 1;
    private Long selfId;
    private Integer type = -1;
    private String auctions = "";
    private Long buyId = -1L;
    private String key;
    private String name = "";
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
    private Boolean sort = true;

    public String buildUrl() {
        if (page == null || order == null || sort == null || selfId == null) {
            return null;
        }
        rnd = new Random().nextDouble();
        String format = "?page=%s&selfid=%s&type=%s&Auctions=%s&buyID=%s&key=%s&name=%s&userId=%s&rnd=%s&pay=%s&order=%s&sort=%s";
        return URL + String.format(format, page, selfId, type, auctions, buyId, key, name, userId, rnd, pay, order, sort).replace("%", "%25");
    }
}
