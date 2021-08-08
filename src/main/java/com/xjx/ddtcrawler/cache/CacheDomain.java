package com.xjx.ddtcrawler.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author XieJiaxing
 * @date 2021/8/7 17:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheDomain {
    private Object object;
    private Long expireAt;
}
