package com.xjx.ddtcrawler.http;

import com.xjx.ddtcrawler.http.enumeration.MethodEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/1/31 2:41
 */
public class HttpHelper {
    private CloseableHttpClient httpClient;
    private HttpRequestMaker httpRequestMaker;
    private CookieStore httpCookieStore;

    public HttpHelper(HttpRequestMaker httpRequestMaker) {
        this.httpRequestMaker = httpRequestMaker;
        httpCookieStore = new BasicCookieStore();
        this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).setDefaultCookieStore(httpCookieStore).build();
    }

    public HttpHelper(String url, MethodEnum methodEnum) {
        this(HttpRequestMaker.makeHttpHelper(url, methodEnum));
    }

    public HttpHelper(String url) {
        this(url, MethodEnum.METHOD_GET);
    }

    public static HttpHelper makeDefaultGetHttpHelper(String url) {
        return new HttpHelper(HttpRequestMaker.makeHttpHelper(url, MethodEnum.METHOD_GET).buildDefaultTimeoutConfig());
    }

    public static HttpHelper makeDefaultTimeoutHttpHelper(String url, MethodEnum methodEnum) {
        return new HttpHelper(HttpRequestMaker.makeHttpHelper(url, methodEnum).buildDefaultTimeoutConfig());
    }

    public static HttpHelper makeDefaultTimeoutHttpHelper(HttpRequestMaker httpRequestMaker) {
        if (httpRequestMaker == null) {
            return null;
        }
        return new HttpHelper(httpRequestMaker.buildDefaultTimeoutConfig());
    }

    public static HttpHelper makeHttpHelper(HttpRequestMaker httpRequestMaker) {
        return new HttpHelper(httpRequestMaker);
    }

    public void setProxy(HttpHost proxy) {
        httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
    }

    public void setHeader(String header, String value) {
        httpRequestMaker.setHeader(header, value);
    }

    public Header[] getHeader() {
        return httpRequestMaker.getAllHeaders();
    }

    public void setUrlEncodedFormPostBody(List<NameValuePair> pairs) {
        if (CollectionUtils.isEmpty(pairs)) {
            return;
        }

        UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8);
        httpRequestMaker.setEntity(httpEntity);
    }

    public void setPostBody(String body) {
        httpRequestMaker.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
    }

    public void setPostBody(String body, ContentType contentType) {
        ContentType ct = ContentType.create(contentType.getMimeType(), StandardCharsets.UTF_8);
        StringEntity stringEntity = new StringEntity(body, ct);
        httpRequestMaker.setEntity(stringEntity);
    }

    public void setHeader(Header header) {
        if (header == null) {
            return;
        }

        httpRequestMaker.setHeader(header.getName(), header.getValue());
    }

    public void setHeaders(List<Header> headers) {
        if (CollectionUtils.isEmpty(headers)) {
            return;
        }

        for (Header header : headers) {
            httpRequestMaker.setHeader(header);
        }
    }

    public void setCookieHeader(String value) {
        setHeader("cookie", value);
    }

    public void setCookieHeader(Cookie cookie) {
        if (cookie == null) {
            return;
        }
        setCookieHeader(cookie.getName() + "=" + cookie.getValue());
    }

    public void setCookieHeaders(List<Cookie> cookies) {
        if (CollectionUtils.isEmpty(cookies)) {
            return;
        }

        for (Cookie cookie : cookies) {
            setCookieHeader(cookie);
        }
    }

    public HttpResponseHelper request() {
        CloseableHttpResponse response = null;
        HttpResponseHelper httpResponseHelper;
        IOException ee = null;
        try {
            response = httpClient.execute(httpRequestMaker);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            ee = e;
        } finally {
            httpResponseHelper = new HttpResponseHelper(httpCookieStore, response, ee);
            HttpClientUtils.closeQuietly(httpClient);
            HttpClientUtils.closeQuietly(response);
        }
        return httpResponseHelper;
    }
}
