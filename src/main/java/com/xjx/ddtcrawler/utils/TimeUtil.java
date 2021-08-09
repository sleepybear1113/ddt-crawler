package com.xjx.ddtcrawler.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author XieJiaxing
 * @date 2021/8/7 14:01
 */
public class TimeUtil {
    private static final String SDF_PATTEN = "yyyy-MM-dd HH:mm:ss";
    private static final TimeZone TZ = TimeZone.getTimeZone("Asia/Shanghai");
    private static final SimpleDateFormat SDF = new SimpleDateFormat(SDF_PATTEN);

    /**
     * 字符串转 {@link Date}
     *
     * @param s 字符串
     * @return {@link Date}
     */
    public static Date convertStringDate(String s) {
        SDF.setTimeZone(TZ);
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            return SDF.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串转 13 位时间戳
     *
     * @param s 字符串
     * @return 时间戳
     */
    public static long convertStringTimestamp(String s) {
        Date date = convertStringDate(s);
        if (date == null) {
            return 0L;
        }

        return date.getTime();
    }
}
