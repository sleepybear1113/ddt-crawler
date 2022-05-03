package com.xjx.ddtcrawler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xjx
 */
@MapperScan(basePackages = {"com.xjx.ddtcrawler.mapper"})
@SpringBootApplication
public class DdtCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DdtCrawlerApplication.class, args);
    }

}
