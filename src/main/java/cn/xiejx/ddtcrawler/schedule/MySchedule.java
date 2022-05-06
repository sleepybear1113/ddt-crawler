package cn.xiejx.ddtcrawler.schedule;

import cn.xiejx.ddtcrawler.dto.TemplateDto;
import cn.xiejx.ddtcrawler.service.TemplateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

    @Resource
    private TemplateService templateService;

    @Scheduled(cron = "0 */1 * * * ?")
    public void mySqlConnection() {
        try {
            TemplateDto templateDto = templateService.getById((long) Math.abs(RANDOM.nextInt(100000)));
            log.info("test connection");
        } catch (Exception e) {
            log.error("test connection error", e);
        }
    }
}
