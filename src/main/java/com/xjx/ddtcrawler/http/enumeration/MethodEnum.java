package com.xjx.ddtcrawler.http.enumeration;

/**
 * HTTP 方法枚举
 *
 * @author XJX
 * @date 2021/1/30 16:53
 */
public enum MethodEnum {
    /**
     * HTTP 的 get 方法
     */
    METHOD_GET("GET"),
    /**
     * HTTP 的 post 方法
     */
    METHOD_POST("POST"),
    /**
     * HTTP 的 delete 方法
     */
    METHOD_DELETE("DELETE"),
    /**
     * HTTP 的 put 方法
     */
    METHOD_PUT("PUT"),
    /**
     * HTTP 的 head 方法
     */
    METHOD_HEAD("HEAD"),
    /**
     * HTTP 的 patch 方法
     */
    METHOD_PATCH("PATCH"),
    /**
     * HTTP 的 options 方法
     */
    METHOD_OPTIONS("OPTIONS"),
    /**
     * HTTP 的 trace 方法
     */
    METHOD_TRACE("TRACE");

    private final String method;

    MethodEnum(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static MethodEnum getByMethodName(String methodName) {
        if (METHOD_GET.getMethod().equals(methodName)) {
            return METHOD_GET;
        } else if (METHOD_POST.getMethod().equals(methodName)) {
            return METHOD_POST;
        } else if (METHOD_PUT.getMethod().equals(methodName)) {
            return METHOD_PUT;
        } else if (METHOD_DELETE.getMethod().equals(methodName)) {
            return METHOD_DELETE;
        } else if (METHOD_HEAD.getMethod().equals(methodName)) {
            return METHOD_HEAD;
        } else if (METHOD_PATCH.getMethod().equals(methodName)) {
            return METHOD_PATCH;
        } else if (METHOD_OPTIONS.getMethod().equals(methodName)) {
            return METHOD_OPTIONS;
        } else if (METHOD_TRACE.getMethod().equals(methodName)) {
            return METHOD_TRACE;
        }
        return null;
    }
}
