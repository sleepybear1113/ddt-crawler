package cn.xiejx.ddtcrawler.interceptor;

import cn.xiejx.cacher.Cacher;
import cn.xiejx.ddtcrawler.config.Ioc;
import cn.xiejx.ddtcrawler.cookie.CookieHelper;
import cn.xiejx.ddtcrawler.cookie.WebUser;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/04 01:09
 */
@Component
public class UserInterceptor implements HandlerInterceptor {

    @Qualifier(Ioc.COMMON_CACHER_NAME)
    @Resource
    private Cacher<String, WebUser> cacher;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Cookie cookie = CookieHelper.getCookie(request);
        if (cookie == null) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        // 获取 cookie 中的用户信息，格式为： WebUserId
        String webUserId = cookie.getValue();
        if (webUserId == null) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        WebUser cachedWebUser = cacher.get(webUserId);
        if (cachedWebUser == null) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        long now = System.currentTimeMillis();
        Long loginTime = cachedWebUser.getLoginTime();
        if (loginTime == null) {
            cachedWebUser.setLoginTime(now);
        }

        WebUser.setWebUser(cachedWebUser);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WebUser.remove();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
