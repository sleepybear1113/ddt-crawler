package com.xjx.ddtcrawler.test;

import com.xjx.ddtcrawler.cookie.CookieHelper;
import com.xjx.ddtcrawler.utils.TimeUtil;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author XieJiaxing
 * @date 2021/8/1 15:25
 */
public class Test {
    public static void main(String[] args) {
        String s = TimeUtil.timestampToString(System.currentTimeMillis());
        System.out.println(s);

    }
}
