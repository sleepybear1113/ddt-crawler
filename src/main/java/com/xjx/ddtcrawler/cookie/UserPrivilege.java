package com.xjx.ddtcrawler.cookie;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xjx
 */
@Component
@Data
public class UserPrivilege {

    @Value("${user-id.admin}")
    private List<Long> adminUserIds;

    @Value("${user-id.common}")
    private List<Long> commonUserIds;

    public boolean isAdmin(WebUser webUser) {
        return !webUser.isTemporaryUser() && isInAdminUserId(webUser.getUserId());
    }

    public boolean notAdmin(WebUser webUser) {
        return !isAdmin(webUser);
    }

    public boolean isInAdminUserId(Long userId) {
        if (userId == null) {
            return false;
        }

        for (Long adminUserId : adminUserIds) {
            if (adminUserId.equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInSpecificUserId(Long userId) {
        if (userId == null) {
            return false;
        }
        for (Long commonUserId : commonUserIds) {
            if (commonUserId.equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
