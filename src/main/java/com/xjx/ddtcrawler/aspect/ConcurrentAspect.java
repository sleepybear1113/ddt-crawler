package com.xjx.ddtcrawler.aspect;

import com.xjx.ddtcrawler.cache.CommonCache;
import com.xjx.ddtcrawler.cookie.WebUser;
import com.xjx.ddtcrawler.exception.MyException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author XieJiaxing
 * @date 2021/8/15 2:01
 */
@Component
@Aspect
public class ConcurrentAspect {
    private static final String LAST_REQUEST_TIME_PREFIX = "LAST_REQUEST_TIME_";

    @Resource
    private HttpServletRequest request;
    @Resource
    private CommonCache commonCache;

    @Around("execution(* com.xjx.ddtcrawler.controller..*(..))")
    public Object aroundMethod(ProceedingJoinPoint pjd) throws Throwable {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return pjd.proceed();
        }
        WebUser webUser = WebUser.getWebUser();
        if (webUser == null) {
            return pjd.proceed();
        }
        String id = webUser.getId();
        if (StringUtils.isBlank(id)) {
            return pjd.proceed();
        }

        long now = System.currentTimeMillis();
        Long concurrentTime = webUser.getConcurrentTime();
        if (concurrentTime != null) {
            // 获取上一次的请求时间
            Long lastRequestTime = commonCache.getCache(getLastRequestTimeKey(id));
            // 设置本次的请求时间和过期时间
            if (lastRequestTime != null) {
                if (lastRequestTime + concurrentTime > now) {
                    throw new MyException("并发限制，限制请求间隔为 " + concurrentTime + " 毫秒");
                }
            }
            commonCache.setCache(getLastRequestTimeKey(id), now, concurrentTime);
        }

        return pjd.proceed();
    }


    private static String getLastRequestTimeKey(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        return LAST_REQUEST_TIME_PREFIX + id;
    }
}
