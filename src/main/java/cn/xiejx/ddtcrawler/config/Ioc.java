package cn.xiejx.ddtcrawler.config;

import cn.xiejx.cacher.Cacher;
import cn.xiejx.cacher.CacherBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/05 02:06
 */
@Configuration
public class Ioc {
    public static final String COMMON_CACHER_NAME = "commonCacher";

    @Bean(COMMON_CACHER_NAME)
    public <K, V> Cacher<K, V> a() {
        return new CacherBuilder().showAllLogs().scheduleName(COMMON_CACHER_NAME).delay(60, TimeUnit.SECONDS).build();
    }
}
