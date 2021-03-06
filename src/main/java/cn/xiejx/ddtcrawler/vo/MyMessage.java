package cn.xiejx.ddtcrawler.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author XJX
 * @date 2021/8/14 20:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 6816947257207776278L;

    private String message;
}
