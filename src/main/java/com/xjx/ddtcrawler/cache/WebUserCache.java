package com.xjx.ddtcrawler.cache;

import com.xjx.ddtcrawler.cookie.WebUser;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XJX
 * @date 2021/8/1 22:56
 */
@Component
public class WebUserCache implements CacheInterface {
    private final static Map<String, WebUser> MAP = new ConcurrentHashMap<>();

    public WebUser getById(String id) {
        if (id == null) {
            return null;
        }
        WebUser webUser = MAP.get(id);
        if (webUser == null) {
            return null;
        }

        Long expireTimeAt = webUser.getExpireAt();
        if (expireTimeAt == null) {
            return webUser;
        } else {
            if (System.currentTimeMillis() > expireTimeAt) {
                MAP.remove(id);
                return null;
            }
            return webUser;
        }
    }

    /**
     * 入参 userId、key、expireTime，
     *
     * @param webUser webUser
     */
    public String saveUser(WebUser webUser) {
        if (webUser == null) {
            return null;
        }

        Long userId = webUser.getUserId();
        String key = webUser.getKey();
        Long expireTimeAt = webUser.getExpireAt();
        if (userId == null || StringUtils.isBlank(key)) {
            return null;
        }

        // id 可能会有
        String id = webUser.getId();
        if (StringUtils.isBlank(id)) {
            // 如果没有的话，表示第一次使用
            long loginTime = System.currentTimeMillis();
            webUser.setLoginTime(loginTime);
            id = webUser.getId();
        }

        // 如果设置了过期时间
        if (expireTimeAt != null) {
            if (expireTimeAt < System.currentTimeMillis()) {
                if (id != null) {
                    MAP.remove(id);
                }
                return null;
            }
        }

        MAP.put(id, webUser);
        return id;
    }

    public void delete(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }

        MAP.remove(id);
    }

    @Override
    public void clearExpired() {
        if (MapUtils.isEmpty(MAP)) {
            return;
        }

        MAP.values().removeIf(CacheDomain::isExpired);
    }
}
