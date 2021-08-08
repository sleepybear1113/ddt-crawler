package com.xjx.ddtcrawler.http;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/4/18 12:11
 */
public class HttpResponseHelper {
    private CookieStore httpCookieStore;
    private Header[] headers;
    private StatusLine statusLine;
    private String responseBody;
    private String exceptionMessage;

    public HttpResponseHelper(CookieStore httpCookieStore, Header[] headers, StatusLine statusLine, String responseBody) {
        this.httpCookieStore = httpCookieStore;
        this.headers = headers;
        this.statusLine = statusLine;
        this.responseBody = responseBody;
    }

    public HttpResponseHelper(CookieStore httpCookieStore, CloseableHttpResponse closeableHttpResponse, IOException ioException) {
        this.httpCookieStore = httpCookieStore;
        if (ioException != null) {
            exceptionMessage = ioException.getMessage();
        }

        if (closeableHttpResponse != null) {
            this.statusLine = closeableHttpResponse.getStatusLine();
            this.headers = closeableHttpResponse.getAllHeaders();
            try {
                this.responseBody = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                this.exceptionMessage = e.getMessage();
            }
        }
    }

    public CookieStore getHttpCookieStore() {
        return httpCookieStore;
    }

    public void setHttpCookieStore(CookieStore httpCookieStore) {
        this.httpCookieStore = httpCookieStore;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public String toString() {
        return "HttpResponseHelper{" +
                "httpCookieStore=" + httpCookieStore +
                ", headers=" + Arrays.toString(headers) +
                ", statusLine=" + statusLine +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}
