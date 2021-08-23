package com.xjx.ddtcrawler.logic;

import com.xjx.ddtcrawler.cache.WebUserCache;
import com.xjx.ddtcrawler.cookie.CookieHelper;
import com.xjx.ddtcrawler.cookie.WebUser;
import com.xjx.ddtcrawler.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author XieJiaxing
 * @date 2021/8/14 16:32
 */
@Component
@Slf4j
public class WebUserLogic {
    @Autowired
    private WebUserCache webUserCache;

    public void save(Long userId, String key, Long expireTime) throws MyException {
        if (userId == null) {
            log.info("userId 为空");
            return;
        }
        if (!WebUser.isInSpecificUserId(userId)) {
            throw new MyException("暂不支持其他id登录");
        }
        if (StringUtils.isBlank(key)) {
            log.info("key 为空");
            return;
        }

        long now = System.currentTimeMillis();
        WebUser webUser = new WebUser();
        webUser.setUserId(userId);
        webUser.setKey(key);
        webUser.setLoginTime(now);

        int maxAge = CookieHelper.COOKIE_MAX_AGE;
        if (expireTime != null) {
            webUser.setExpireAt(expireTime + now);
            maxAge = (int) (expireTime / 1000);
        }
        String id = webUserCache.saveUser(webUser);
        CookieHelper.setWebUserCookie(id, maxAge);
    }

    public String generateTemporaryLicense(WebUser temporaryWebUser) {
        if (temporaryWebUser == null) {
            return null;
        }
        Long userId = temporaryWebUser.getUserId();
        if (userId == null) {
            return null;
        }

        String temporaryLicense = RandomStringUtils.random(10, true, true);
        temporaryWebUser.setId(temporaryLicense);
        temporaryWebUser.setTemporaryLicense(temporaryLicense);
        webUserCache.saveUser(temporaryWebUser);
        return temporaryLicense;
    }

    public void deleteTemporaryLicense(WebUser webUser, String temporaryLicense) throws MyException {
        if (webUser == null || StringUtils.isBlank(temporaryLicense)) {
            return;
        }
        Long userId = webUser.getUserId();

        WebUser cachedWebUser = webUserCache.getById(temporaryLicense);
        if (cachedWebUser == null) {
            throw new MyException("找不到该License");
        }
        Long cachedUserId = cachedWebUser.getUserId();

        if (!userId.equals(cachedUserId)) {
            throw new MyException("无权限操作该License");
        }
        webUserCache.delete(temporaryLicense);
        throw new MyException("删除成功");
    }

    public void writeTemporaryLicenseCookie(String temporaryLicense) throws MyException {
        WebUser webUser = webUserCache.getById(temporaryLicense);
        if (webUser == null) {
            throw new MyException("用户不存在或者已过期");
        }
        if (!webUser.isTemporaryUser()) {
            // 没有 License 的 webUser 只能通过 useId、key 登录
            log.info("非法登录: " + temporaryLicense);
            throw new MyException("用户不存在或者已过期");
        }

        int maxAge = CookieHelper.COOKIE_MAX_AGE;
        Long expireTimeAt = webUser.getExpireAt();
        if (expireTimeAt != null) {
            if (expireTimeAt < System.currentTimeMillis()) {
                // webUser 已经过期，删除 cookie
                maxAge = 0;
            } else {
                maxAge = (int) ((expireTimeAt - System.currentTimeMillis()) / 1000);
            }
        }
        CookieHelper.setWebUserCookie(temporaryLicense, maxAge);
    }
}
