package com.xjx.ddtcrawler.task;

import com.xjx.ddtcrawler.domain.Template;
import com.xjx.ddtcrawler.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;

/**
 * @author XieJiaxing
 * @date 2021/8/7 14:45
 */
@Configuration
@EnableScheduling
@Slf4j
public class MySqlConnectionTask {
    @Autowired
    private TemplateService templateService;
    private static final Random RANDOM = new Random();

    @Scheduled(cron = "0 */1 * * * ?")
    private void configureTasks() {
        try {
            Template template = templateService.getById(RANDOM.nextLong() / 1000);
            log.info("test connection");
        } catch (Exception e) {
            log.error("test connection error", e);
        }
    }
}
