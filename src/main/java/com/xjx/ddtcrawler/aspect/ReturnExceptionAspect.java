package com.xjx.ddtcrawler.aspect;

import com.xjx.ddtcrawler.exception.MyException;
import com.xjx.ddtcrawler.vo.ResultCode;
import com.xjx.ddtcrawler.vo.ResultCodeConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author XieJiaxing
 * @date 2021/8/10 0:17
 */
//@Aspect
@Component
@Slf4j
public class ReturnExceptionAspect {
//    @Around("execution(* com.xjx.ddtcrawler.controller..*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object result;
        try {
            result = joinPoint.proceed();
            return new ResultCode(result);
        } catch (MyException e) {
            String message = e.getMessage();
            log.warn(message);
            return new ResultCode(message);
        } catch (Throwable throwable) {
            String message = throwable.getMessage();
            log.error(message, throwable);
            return new ResultCode(ResultCodeConstant.CodeEnum.SYSTEM_ERROR.getCode(), message);
        }
    }
}
