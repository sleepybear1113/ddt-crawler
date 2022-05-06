package cn.xiejx.ddtcrawler.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author XJX
 * @date 2021/8/7 14:01
 */
public class TimeUtil {
    private static final String SDF_PATTEN = "yyyy-MM-dd HH:mm:ss";
    public static final FastDateFormat FAST_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("Asia/Shanghai"));

    public static String timestampToString(Long t) {
        if (t == null) {
            return null;
        }
        return FAST_DATE_FORMAT.format(t);
    }

    /**
     * 字符串转 {@link Date}
     *
     * @param s 字符串
     * @return {@link Date}
     */
    public static Date convertStringDate(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        try {
            return FAST_DATE_FORMAT.parse(s);
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
