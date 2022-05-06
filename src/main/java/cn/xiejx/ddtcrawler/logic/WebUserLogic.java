package cn.xiejx.ddtcrawler.logic;

import cn.xiejx.cacher.Cacher;
import cn.xiejx.ddtcrawler.config.Ioc;
import cn.xiejx.ddtcrawler.constants.Constant;
import cn.xiejx.ddtcrawler.cookie.CookieHelper;
import cn.xiejx.ddtcrawler.cookie.WebUser;
import cn.xiejx.ddtcrawler.exception.MyException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author XJX
 * @date 2021/8/14 16:32
 */
@Component
@Slf4j
public class WebUserLogic {
    @Qualifier(Ioc.COMMON_CACHER_NAME)
    @Resource
    private Cacher<String, WebUser> cacher;

    public void save(Long userId, String key, Long expireTime) throws MyException {
        if (userId == null) {
            log.info("userId 为空");
            return;
        }
        if (!(WebUser.isInSpecificUserId(userId) || WebUser.isInAdminUserId(userId))) {
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
            if (expireTime > Constant.ONE_DAY_MILLISECONDS) {
                expireTime = Constant.ONE_DAY_MILLISECONDS;
            }
            maxAge = (int) (expireTime / 1000);
        } else {
            expireTime = Constant.ONE_DAY_MILLISECONDS;
        }
        webUser.setExpireTime(expireTime);
        String id = webUser.getId();
        cacher.set(id, webUser, expireTime);
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
        temporaryWebUser.setLoginTime(System.currentTimeMillis());
        cacher.set(temporaryWebUser.getId(), temporaryWebUser, temporaryWebUser.getExpireTime());
        return temporaryLicense;
    }

    public void deleteTemporaryLicense(WebUser webUser, String temporaryLicense) throws MyException {
        if (webUser == null || StringUtils.isBlank(temporaryLicense)) {
            return;
        }
        Long userId = webUser.getUserId();

        WebUser cachedWebUser = cacher.get(temporaryLicense);
        if (cachedWebUser == null) {
            throw new MyException("找不到该License");
        }
        Long cachedUserId = cachedWebUser.getUserId();

        if (!userId.equals(cachedUserId)) {
            throw new MyException("无权限操作该License");
        }
        cacher.remove(temporaryLicense);
        throw new MyException("删除成功");
    }

    public void writeTemporaryLicenseCookie(String temporaryLicense) throws MyException {
        WebUser webUser = cacher.get(temporaryLicense);
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
