package com.xjx.ddtcrawler.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author XieJiaxing
 * @date 2021/8/14 15:59
 */
@Configuration
public class FilterRegistration {
    @Bean
    public FilterRegistrationBean<WebUserFilter> myFilter() {
        FilterRegistrationBean<WebUserFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebUserFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
