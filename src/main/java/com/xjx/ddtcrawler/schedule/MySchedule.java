package com.xjx.ddtcrawler.schedule;

import com.xjx.ddtcrawler.cache.CacheInterface;
import com.xjx.ddtcrawler.domain.Template;
import com.xjx.ddtcrawler.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;

/**
 * @author XJX
 * @date 2021/8/21 17:29
 */
@Configuration
@EnableScheduling
@Slf4j
public class MySchedule {
    private static final Random RANDOM = new Random();

    @Autowired
    private TemplateService templateService;
    @Autowired
    @Qualifier("commonCache")
    private CacheInterface commonCache;
    @Autowired
    @Qualifier("webUserCache")
    private CacheInterface webUserCache;

    @Scheduled(cron = "0 */1 * * * ?")
    public void mySqlConnection() {
        try {
            Template template = templateService.getById(RANDOM.nextLong() / 1000);
            log.info("test connection");
        } catch (Exception e) {
            log.error("test connection error", e);
        }
    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void clearExpireCache() {
        commonCache.clearExpired();
        webUserCache.clearExpired();
        log.info("clearExpireCache");
    }
}
