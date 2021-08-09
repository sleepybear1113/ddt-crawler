package com.xjx.ddtcrawler.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author XieJiaxing
 * @date 2021/8/10 0:33
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResultCode {
    private Integer code;
    private String message;
    private Object result;

    public ResultCode(Object result) {
        this.code = ResultCodeConstant.CodeEnum.SUCCESS.getCode();
        this.message = null;
        this.result = result;
    }

    public ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.result = null;
    }

    public ResultCode(String message) {
        this.code = ResultCodeConstant.CodeEnum.COMMON_ERROR.getCode();
        this.message = message;
        this.result = null;
    }
}
