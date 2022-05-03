package com.xjx.ddtcrawler.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjx.ddtcrawler.utils.EncryptedUtils;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author XJX
 * @date 2021/8/1 20:56
 */
@Data
@TableName("template")
public class Template implements Serializable {
    @Serial
    private static final long serialVersionUID = 6076962907927806205L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("name")
    private String name;
    @TableField("price")
    private Double price;
    @TableField("modify_time")
    private Long modifyTime;
    @TableField(exist = false)
    private Boolean isEncrypted = false;

    public void encryptId() {
        if (this.isEncrypted) {
            return;
        }
        this.id = EncryptedUtils.encryptTemplateId(this.id);
        this.isEncrypted = true;
    }

    public void decryptId() {
        if (!this.isEncrypted) {
            return;
        }
        this.id = EncryptedUtils.decryptTemplateId(this.id);
        this.isEncrypted = false;
    }
}
