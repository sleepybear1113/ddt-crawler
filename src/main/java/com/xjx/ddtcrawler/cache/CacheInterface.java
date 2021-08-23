package com.xjx.ddtcrawler.cache;

/**
 * @author XieJiaxing
 * @date 2021/8/21 17:37
 */
public interface CacheInterface {
    /**
     * 清理过期的
     */
    void clearExpired();
}
