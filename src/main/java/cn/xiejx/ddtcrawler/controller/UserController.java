package cn.xiejx.ddtcrawler.controller;

import cn.xiejx.ddtcrawler.cookie.WebUser;
import cn.xiejx.ddtcrawler.cookie.CookieHelper;
import cn.xiejx.ddtcrawler.exception.MyException;
import cn.xiejx.ddtcrawler.logic.WebUserLogic;
import cn.xiejx.ddtcrawler.utils.TimeUtil;
import cn.xiejx.ddtcrawler.vo.MyMessage;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XJX
 * @date 2021/8/14 18:31
 */
@RestController
public class UserController {
    @Resource
    private WebUserLogic webUserLogic;

    @RequestMapping("/user/saveUser")
    public Boolean saveUser(Long userId, String key, Long expireTime, String temporaryLicense) throws MyException {
        if (userId == null && StringUtils.isBlank(temporaryLicense)) {
            return false;
        }
        if (StringUtils.isBlank(temporaryLicense)) {
            // 直接用户名+ key 的用户，高权限
            webUserLogic.save(userId, key, expireTime);
        } else {
            webUserLogic.writeTemporaryLicenseCookie(temporaryLicense);
        }
        return true;
    }

    @RequestMapping("/user/getUser")
    public WebUser getUserInfo() throws MyException {
        WebUser webUser = WebUser.getSafeWebUser();
        webUser.setKey(null);
        webUser.setUserId(null);
        return webUser;
    }

    @RequestMapping("/user/createTemporaryLicense")
    public MyMessage createTemporaryLicense(Long expireTime, @RequestParam(required = false, defaultValue = "5000") Long concurrentTime) throws MyException {
        WebUser webUser = WebUser.getSafeWebUser();
        if (webUser.isTemporaryUser()) {
            throw new MyException("权限不够，请不要使用License账户创建临时License");
        }

        long minConcurrentTime = 100L;
        if (concurrentTime < webUser.getConcurrentTime()) {
            concurrentTime = webUser.getConcurrentTime();
        }
        if (concurrentTime < minConcurrentTime) {
            concurrentTime = minConcurrentTime;
        }

        long now = System.currentTimeMillis();
        webUser.setExpireTime(expireTime);
        webUser.setConcurrentTime(concurrentTime);
        String temporaryLicense = webUserLogic.generateTemporaryLicense(webUser);
        if (expireTime == null) {
            return new MyMessage(String.format("临时License: %s，不过期", temporaryLicense));
        }
        return new MyMessage(String.format("临时License: %s，在%s秒后过期，过期时间：%s", temporaryLicense, expireTime, TimeUtil.timestampToString(expireTime * 1000 + now)));
    }

    @RequestMapping("/user/deleteTemporaryLicense")
    public void deleteTemporaryLicense(String temporaryLicense) throws MyException {
        WebUser webUser = WebUser.getSafeWebUser();
        webUserLogic.deleteTemporaryLicense(webUser, temporaryLicense);
    }

    @RequestMapping("/user/deleteCookie")
    public void deleteCookie() {
        CookieHelper.setWebUserCookie("", 0);
    }
}
