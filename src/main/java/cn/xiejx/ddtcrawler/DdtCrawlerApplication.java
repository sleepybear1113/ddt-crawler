package cn.xiejx.ddtcrawler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xjx
 */
@MapperScan(basePackages = {"cn.xiejx.ddtcrawler.mapper"})
@SpringBootApplication
public class DdtCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DdtCrawlerApplication.class, args);
    }

}
