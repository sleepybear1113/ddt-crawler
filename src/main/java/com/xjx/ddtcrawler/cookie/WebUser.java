package com.xjx.ddtcrawler.cookie;

import com.xjx.ddtcrawler.cache.CacheDomain;
import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.utils.EncryptedUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author XJX
 * @date 2021/8/14 12:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WebUser extends CacheDomain implements Serializable {
    private static final long serialVersionUID = 5392034256168678992L;

    public static final ThreadLocal<WebUser> WEB_USER_THREAD_LOCAL = new ThreadLocal<>();

    private String id;
    private Long loginTime;
    private Long userId;
    private String key;
    private String temporaryLicense;
    /**
     * 用户并发毫秒
     */
    private Long concurrentTime = 500L;

    public static void setWebUser(WebUser webUser) {
        WEB_USER_THREAD_LOCAL.set(webUser);
    }

    public static WebUser getWebUser() {
        return WEB_USER_THREAD_LOCAL.get();
    }

    public static WebUser getSafeWebUser() throws MyException {
        WebUser webUser = getWebUser();
        if (webUser == null) {
            throw new MyException("用户不存在或者已过期");
        }
        Long expireTimeAt = webUser.getExpireAt();
        if (expireTimeAt != null && expireTimeAt < System.currentTimeMillis()) {
            throw new MyException("用户不存在或者已过期");
        }
        WebUser res = new WebUser();
        BeanUtils.copyProperties(webUser, res);
        return res;
    }

    public boolean isTemporaryUser() {
        return StringUtils.isNotBlank(this.temporaryLicense);
    }

    public String getId() {
        if (StringUtils.isNotBlank(this.id)) {
            return this.id;
        }

        if (this.loginTime == null) {
            this.loginTime = System.currentTimeMillis();
        }

        this.id = EncryptedUtils.encryptId(this.getUserId(), this.loginTime);
        return this.id;
    }

    public boolean isAdmin() {
        return isInSpecificUserId(this.userId) && !isTemporaryUser();
    }

    public static boolean isInSpecificUserId(Long userId) {
        if (userId == null) {
            return false;
        }
        long s001 = 45801399L;
        long h2 = 45809800L;
        return s001 == userId || h2 == userId;
    }
}
