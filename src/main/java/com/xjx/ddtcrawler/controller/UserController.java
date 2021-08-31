package com.xjx.ddtcrawler.controller;

import com.xjx.ddtcrawler.cookie.CookieHelper;
import com.xjx.ddtcrawler.cookie.WebUser;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.logic.WebUserLogic;
import com.xjx.ddtcrawler.utils.TimeUtil;
import com.xjx.ddtcrawler.vo.MyMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XJX
 * @date 2021/8/14 18:31
 */
@RestController
public class UserController {
    @Autowired
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
        if (expireTime != null) {
            webUser.setExpireAt(expireTime * 1000 + now);
        }
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
    public void deleteCookie() throws MyException {
        CookieHelper.setWebUserCookie("", 0);
    }
}
