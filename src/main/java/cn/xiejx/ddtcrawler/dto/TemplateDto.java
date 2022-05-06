package cn.xiejx.ddtcrawler.dto;

import cn.xiejx.ddtcrawler.utils.EncryptedUtils;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/03 23:59
 */
@Data
public class TemplateDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -5008132148465573140L;

    private Long id;
    private Long modifyTime;

    private String name;
    private Double price;
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
