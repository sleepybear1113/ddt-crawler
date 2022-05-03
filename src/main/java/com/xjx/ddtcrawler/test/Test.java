package com.xjx.ddtcrawler.test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author XJX
 * @date 2021/8/1 15:25
 */
public class Test {
    public static void main(String[] args) {
        CacheLoader<String, String> loader = new CacheLoader<>() {
            @Override
            @Nonnull
            public String load(@Nonnull String key) {
                checkNotNull(key);
                return "null";
            }
        };

        Cache<String, String> cache = CacheBuilder.newBuilder().recordStats().expireAfterWrite(300, TimeUnit.MILLISECONDS).build();

        cache.put("2", "2");
        cache.cleanUp();
        cache.invalidateAll();
        String ifPresent = cache.getIfPresent("1");
        String w2 = cache.getIfPresent("2");
        String w3 = cache.getIfPresent("3");
        System.out.println(w3);
        CacheStats stats = cache.stats();
        System.out.println(stats);

    }
}
