package com.xjx.ddtcrawler.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author XieJiaxing
 * @date 2021/8/7 17:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = -1617868566077296126L;

    private Object object;
    private Long expireAt;

    public boolean isExpired() {
        if (expireAt == null) {
            return false;
        }
        return expireAt < System.currentTimeMillis();
    }
}
