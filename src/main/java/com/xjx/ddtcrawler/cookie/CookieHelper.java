package com.xjx.ddtcrawler.cookie;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author XJX
 * @date 2021/8/14 12:29
 */
public class CookieHelper {
    public static final String DDT_COOKIE_NAME = "DDT_USER_INFO";
    public static final int COOKIE_MAX_AGE = 2 * 7 * 24 * 3600;

    public static Cookie getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (DDT_COOKIE_NAME.equals(name)) {
                return cookie;
            }
        }

        return null;
    }

    public static void setWebUserCookie(HttpServletResponse response, String cookieValue, int expireTime) {
        if (cookieValue == null || response == null) {
            return;
        }

        Cookie cookie = new Cookie(DDT_COOKIE_NAME, cookieValue);
        cookie.setPath("/");
        cookie.setMaxAge(expireTime);
        response.addCookie(cookie);
    }

    public static void setWebUserCookie(String cookieValue, int expireTime) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
        setWebUserCookie(response, cookieValue, expireTime);

    }

    public static void setWebUserCookie(String encryptUserId, String key) {
        String s = "";
        if (StringUtils.isNotBlank(encryptUserId)) {
            s += encryptUserId;
        }
        s += "-";
        if (StringUtils.isNotBlank(key)) {
        }
        setWebUserCookie(s, COOKIE_MAX_AGE);
    }
}
