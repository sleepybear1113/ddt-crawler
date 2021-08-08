package com.xjx.ddtcrawler.cache;

import com.xjx.ddtcrawler.domain.UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XieJiaxing
 * @date 2021/8/1 22:56
 */
@Component
public class UserInfoCache {
    private final static Map<Long, UserInfo> MAP = new ConcurrentHashMap<>();

    public UserInfo getByUserId(Long id) {
        return MAP.get(id);
    }

    public void saveUser(Long id, String key) {
        UserInfo userInfo = MAP.get(id);
        if (userInfo == null) {
            MAP.put(id, new UserInfo(id, key));
        } else {
            userInfo.setKey(key);
        }
    }
}
