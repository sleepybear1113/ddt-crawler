package com.xjx.ddtcrawler.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.xjx.ddtcrawler.utils.EncryptedUtils;
import com.xjx.ddtcrawler.utils.TimeUtil;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author XJX
 * @date 2021/8/1 16:58
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@TableName("item")
public class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 5112946113341660069L;

    private Long id;
    @XStreamAlias("AuctionID")
    @XStreamAsAttribute
    private Long auctionId;

    @XStreamAlias("AuctioneerID")
    @XStreamAsAttribute
    private Long auctioneerId;

    @XStreamAlias("AuctioneerName")
    @XStreamAsAttribute
    private String auctioneerName;

    /**
     * 原始数据，字符串时间
     */
    @XStreamAlias("BeginDate")
    @XStreamAsAttribute
    private String beginTimeString;

    /**
     * 时间戳
     */
    private Long beginTime;
    /**
     * date 类型时间
     */
    private Date beginDate;

    @XStreamAlias("BuyerID")
    @XStreamAsAttribute
    private Long buyerId;

    @XStreamAlias("BuyerName")
    @XStreamAsAttribute
    private String buyerName;

    @XStreamAlias("ItemID")
    @XStreamAsAttribute
    private Long itemId;

    @XStreamAlias("Mouthful")
    @XStreamAsAttribute
    private Long mouthful;

    @XStreamAlias("PayType")
    @XStreamAsAttribute
    private Integer payType;

    @XStreamAlias("Price")
    @XStreamAsAttribute
    private Long price;

    @XStreamAlias("Rise")
    @XStreamAsAttribute
    private Long rise;

    @XStreamAlias("Pic")
    @XStreamAsAttribute
    private String pic;
    /**
     * 拍卖场字段
     */
    @XStreamAlias("ValidDate")
    @XStreamAsAttribute
    private Integer validDate;

    /**
     * 拍卖有效期
     */
    private Integer auctionDate;
    /**
     * 物品有效期
     */
    private Integer itemDate;

    @XStreamAlias("AgilityCompose")
    @XStreamAsAttribute
    private Long agilityCompose;

    @XStreamAlias("AttackCompose")
    @XStreamAsAttribute
    private Long attackCompose;

    @XStreamAlias("Color")
    @XStreamAsAttribute
    private String color;

    @XStreamAlias("Skin")
    @XStreamAsAttribute
    private String skin;

    @XStreamAlias("Count")
    @XStreamAsAttribute
    private Long count;

    @XStreamAlias("DefendCompose")
    @XStreamAsAttribute
    private Long defendCompose;

    @XStreamAlias("IsBinds")
    @XStreamAsAttribute
    private Boolean isBinds;

    @XStreamAlias("IsUsed")
    @XStreamAsAttribute
    private Boolean isUsed;

    @XStreamAlias("IsJudge")
    @XStreamAsAttribute
    private Boolean isJudge;

    @XStreamAlias("LuckCompose")
    @XStreamAsAttribute
    private Long luckCompose;

    @XStreamAlias("Place")
    @XStreamAsAttribute
    private Long place;

    @XStreamAlias("StrengthenLevel")
    @XStreamAsAttribute
    private Long strengthenLevel;

    @XStreamAlias("TemplateID")
    @XStreamAsAttribute
    private Long templateId;

    @XStreamAlias("UserID")
    @XStreamAsAttribute
    private Long userId;

    @XStreamAlias("Item")
    @XStreamAsAttribute
    private Item item;

    private String templateName;
    private Double unitPrice;
    private Double unitMouthfulPrice;
    /**
     * 用户定义的低价
     */
    private Double userDefinePrice;
    private Boolean isTemplateIdEncrypted = false;

    public void encryptTemplateId() {
        if (this.isTemplateIdEncrypted == null) {
            this.isTemplateIdEncrypted = false;
        }
        if (this.isTemplateIdEncrypted) {
            return;
        }
        this.templateId = EncryptedUtils.encryptTemplateId(this.templateId);
        this.isTemplateIdEncrypted = true;
    }

    public void decryptTemplateId() {
        if (this.isTemplateIdEncrypted == null) {
            this.isTemplateIdEncrypted = false;
        }
        if (!this.isTemplateIdEncrypted) {
            return;
        }
        this.templateId = EncryptedUtils.decryptTemplateId(this.templateId);
        this.isTemplateIdEncrypted = false;
    }

    @Override
    public String toString() {
        return "Item{" +
                "auctionId=" + auctionId +
                ", auctioneerId=" + auctioneerId +
                ", auctioneerName='" + auctioneerName + '\'' +
                ", beginTimeString='" + beginTimeString + '\'' +
                ", beginTime=" + beginTime +
                ", beginTimeString=" + beginDate +
                ", buyerId=" + buyerId +
                ", buyerName='" + buyerName + '\'' +
                ", itemId=" + itemId +
                ", mouthful=" + mouthful +
                ", payType=" + payType +
                ", price=" + price +
                ", pic=" + pic +
                ", rise=" + rise +
                ", validDate=" + validDate +
                ", auctionDate=" + auctionDate +
                ", itemDate=" + itemDate +
                ", agilityCompose=" + agilityCompose +
                ", attackCompose=" + attackCompose +
                ", color='" + color + '\'' +
                ", skin='" + skin + '\'' +
                ", count=" + count +
                ", defendCompose=" + defendCompose +
                ", isBinds=" + isBinds +
                ", isUsed=" + isUsed +
                ", isJudge=" + isJudge +
                ", luckCompose=" + luckCompose +
                ", place=" + place +
                ", strengthenLevel=" + strengthenLevel +
                ", templateId=" + templateId +
                ", userId=" + userId +
                ", item=" + item +
                ", templateName='" + templateName + '\'' +
                ", unitPrice=" + unitPrice +
                ", unitMouthfulPrice=" + unitMouthfulPrice +
                '}';
    }

    public void buildSelf() {
        Item subItem = this.item;
        if (subItem != null) {
            this.templateId = subItem.getTemplateId();
            this.count = subItem.getCount();
            this.userId = subItem.getUserId();
            this.itemDate = subItem.getValidDate();
            this.pic = subItem.getPic();

            this.item = null;
        }

        this.unitPrice = (double) this.price / this.count;
        this.unitMouthfulPrice = (double) this.mouthful / this.count;
        if (this.mouthful == 0) {
            this.mouthful = -1L;
            this.unitMouthfulPrice = -1.0;
        }

        this.auctionDate = this.validDate;

        Date beginDate = TimeUtil.convertStringDate(this.beginTimeString);
        this.beginDate = beginDate;
        if (beginDate != null) {
            this.beginTime = beginDate.getTime();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(auctionId, item.auctionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auctionId);
    }
}
