package com.xjx.ddtcrawler.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * @author XieJiaxing
 * @date 2021/8/14 19:20
 */
public class EncryptedUtils {
    public static final Random RANDOM = new Random();
    public static final long KEY = 1000003L;

    public static String encryptId(Long userId, Long loginTime) {
        if (userId == null || loginTime == null) {
            return null;
        }

        long e = userId ^ loginTime;
        return RANDOM.nextInt(9) + "" + e + "" + RANDOM.nextInt(9);
    }

    public static String encryptUserId(Long userId) {
        if (userId == null) {
            return null;
        }

        long e = userId ^ KEY;

        return RANDOM.nextInt(9) + "" + e + "" + RANDOM.nextInt(9);
    }

    public static Long decryptUserId(String s) {
        if (StringUtils.isBlank(s) || s.length() <= 2 || !StringUtils.isNumeric(s)) {
            return null;
        }

        String substring = s.substring(1, s.length() - 1);
        return Long.parseLong(substring) ^ KEY;
    }

    public static Long decryptTemporaryLicense(String s) {
        if (StringUtils.isBlank(s) || s.length() <= 2 || !StringUtils.isNumeric(s)) {
            return null;
        }

        String substring = s.substring(1, s.length() - 1);
        return Long.parseLong(substring) ^ KEY;
    }

    public static Long encryptTemplateId(Long id) {
        String s = encryptUserId(id);
        if (StringUtils.isBlank(s)) {
            return null;
        }

        return Long.valueOf(s);
    }

    public static Long decryptTemplateId(Long id) {
        if (id == null) {
            return null;
        }

        String s = String.valueOf(id);
        if (StringUtils.isBlank(s) || s.length() <= 2 || !StringUtils.isNumeric(s)) {
            return null;
        }

        String substring = s.substring(1, s.length() - 1);
        return Long.parseLong(substring) ^ KEY;
    }
}
