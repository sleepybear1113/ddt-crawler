package com.xjx.ddtcrawler.filter;

import com.xjx.ddtcrawler.cache.WebUserCache;
import com.xjx.ddtcrawler.cookie.CookieHelper;
import com.xjx.ddtcrawler.cookie.WebUser;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author XJX
 * @date 2021/8/14 15:32
 */
@Slf4j
@WebFilter(urlPatterns = "/*", description = "myFilter")
public class WebUserFilter implements Filter {

    private WebUserCache webUserCache;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        if (webApplicationContext == null) {
            return;
        }
        this.webUserCache = webApplicationContext.getBean(WebUserCache.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)  {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            // 读取 cookie
            Cookie cookie = CookieHelper.getCookie(httpServletRequest);
            if (cookie == null) {
                chain.doFilter(request, response);
                return;
            }

            // 获取 cookie 中的用户信息，格式为： WebUserId
            String webUserId = cookie.getValue();
            if (webUserId == null) {
                chain.doFilter(request, response);
                return;
            }

            WebUser cachedWebUser = webUserCache.getById(webUserId);
            if (cachedWebUser == null) {
                chain.doFilter(request, response);
                return;
            }
            long now = System.currentTimeMillis();
            Long loginTime = cachedWebUser.getLoginTime();
            if (loginTime == null) {
                cachedWebUser.setLoginTime(now);
            }

            WebUser.setWebUser(cachedWebUser);
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("WebUserFilter 异常: " + e.getMessage(), e);
        } finally {
            WebUser.setWebUser(null);
        }
    }

}
