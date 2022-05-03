package com.xjx.ddtcrawler.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/03 15:53
 */
@Data
public class BaseDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = -5347501343901713024L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("modify_time")
    private Long modifyTime;
}
