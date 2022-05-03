package com.xjx.ddtcrawler.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjx.ddtcrawler.utils.EncryptedUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author XJX
 * @date 2021/8/1 20:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("template")
public class Template extends BaseDomain {
    @Serial
    private static final long serialVersionUID = 6076962907927806205L;

    @TableField("name")
    private String name;
    @TableField("price")
    private Double price;
    @TableField(exist = false)
    private Boolean isEncrypted = false;

    public void encryptId() {
        if (this.isEncrypted) {
            return;
        }
        this.setId(EncryptedUtils.encryptTemplateId(this.getId()));
        this.isEncrypted = true;
    }

    public void decryptId() {
        if (!this.isEncrypted) {
            return;
        }
        this.setId(EncryptedUtils.decryptTemplateId(this.getId()));
        this.isEncrypted = false;
    }
}
