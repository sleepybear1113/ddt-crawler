package com.xjx.ddtcrawler.domain;

import lombok.Data;

import java.util.Objects;

/**
 * @author XieJiaxing
 * @date 2021/8/1 22:56
 */
@Data
public class UserInfo {
    private Long id;
    private String key;

    public UserInfo() {
    }

    public UserInfo(Long id, String key) {
        this.id = id;
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(id, userInfo.id) && Objects.equals(key, userInfo.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key);
    }
}
