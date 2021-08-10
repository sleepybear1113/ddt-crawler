package com.xjx.ddtcrawler.advice;

import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.vo.ResultCode;
import com.xjx.ddtcrawler.vo.ResultCodeConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author XieJiaxing
 * @date 2021/8/10 1:59
 */
@RestControllerAdvice
@Slf4j
public class ControllerReturnAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o instanceof ResultCode) {
            return o;
        }

        return new ResultCode(o);
    }

    @ExceptionHandler(value = MyException.class)
    public ResultCode handlerGlobeMyException(HttpServletRequest request, MyException exception) {
        return new ResultCode(ResultCodeConstant.CodeEnum.COMMON_ERROR.getCode(), exception.getMessage(), null);
    }

    @ExceptionHandler(value = Exception.class)
    public ResultCode handlerGlobeException(HttpServletRequest request, MyException exception) {
        return new ResultCode(ResultCodeConstant.CodeEnum.SYSTEM_ERROR.getCode(), exception.getMessage(), null);
    }
}