package com.xjx.ddtcrawler.domain.constant;

/**
 * @author XieJiaxing
 * @date 2021/8/1 16:13
 */
public class AuctionConstant {
    /**
     * 排序规则
     */
    public enum OrderEnum {
        /**
         * 物品名排序
         */
        NAME(0),
        /**
         * 时间排序
         */
        TIME(2),
        /**
         * 卖家名称排序
         */
        SELLER(3),
        /**
         * 价格排序
         */
        PRICE(4),
        ;
        private final Integer value;

        OrderEnum(Integer i) {
            this.value = i;
        }

        public Integer getValue() {
            return value;
        }

        public static boolean isInRange(Integer i) {
            for (OrderEnum orderEnum : values()) {
                if (orderEnum.getValue().equals(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 物品类别
     */
    public enum TypeEnum {
        /**
         * 武器
         */
        EQUIPMENT(7),
        /**
         * 副武器
         */
        SECOND_EQUIPMENT(2),
        ;
        private final Integer type;

        TypeEnum(Integer i) {
            this.type = i;
        }

        public Integer getType() {
            return type;
        }
    }
}
