package com.xjx.ddtcrawler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/04 00:18
 */
@Configuration
public class ConfigBean {
    private static List<Long> adminUserIds;
    private static List<Long> commonUserIds;
    private static String gameUrlPrefix;

    public static List<Long> getAdminUserIds() {
        return adminUserIds;
    }

    @Value("${user-id.admin}")
    public void setAdminUserIds(List<Long> adminUserIds) {
        ConfigBean.adminUserIds = adminUserIds;
    }

    public static List<Long> getCommonUserIds() {
        return commonUserIds;
    }

    @Value("${user-id.common}")
    public void setCommonUserIds(List<Long> commonUserIds) {
        ConfigBean.commonUserIds = commonUserIds;
    }

    public static String getGameUrlPrefix() {
        return gameUrlPrefix;
    }

    @Value("${game-url.prefix}")
    public void setGameUrlPrefix(String gameUrlPrefix) {
        ConfigBean.gameUrlPrefix = gameUrlPrefix;
    }
}
