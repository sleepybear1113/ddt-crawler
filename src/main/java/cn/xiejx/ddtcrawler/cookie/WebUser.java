package cn.xiejx.ddtcrawler.cookie;

import cn.xiejx.ddtcrawler.config.ConfigBean;
import cn.xiejx.ddtcrawler.exception.MyException;
import cn.xiejx.ddtcrawler.utils.EncryptedUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author XJX
 * @date 2021/8/14 12:29
 */
@Data
public class WebUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 5392034256168678992L;

    public static final ThreadLocal<WebUser> WEB_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 加密的 userId
     */
    private String id;
    /**
     * 真实 userId
     */
    private Long userId;
    /**
     * key
     */
    private String key;
    /**
     * 登陆时间
     */
    private Long loginTime;
    /**
     * 临时 License
     */
    private String temporaryLicense;
    /**
     * 用户并发毫秒
     */
    private Long concurrentTime = 500L;
    private Long expireTime;

    public static void setWebUser(WebUser webUser) {
        WEB_USER_THREAD_LOCAL.set(webUser);
    }

    public static void remove() {
        WEB_USER_THREAD_LOCAL.remove();
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

    public Long getExpireAt() {
        return this.expireTime + this.loginTime;
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
        return !isTemporaryUser() && isInAdminUserId(this.userId);
    }

    public boolean notAdmin() {
        return !isAdmin();
    }

    public static boolean isInAdminUserId(Long userId) {
        if (userId == null) {
            return false;
        }

        for (Long adminUserId : ConfigBean.getAdminUserIds()) {
            if (adminUserId.equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInSpecificUserId(Long userId) {
        if (userId == null) {
            return false;
        }
        for (Long commonUserId : ConfigBean.getCommonUserIds()) {
            if (commonUserId.equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
