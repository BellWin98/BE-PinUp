package com.pinup.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAop {

    // controller와 service method만 로그 적용
//    @Around("execution(* com.pinup.controller..*(..)) || execution(* com.pinup.service..*(..))")
    @Around("(execution(* com.pinup.controller..*(..)) || execution(* com.pinup.service..*(..))) && !execution(* com.pinup.domain.home.HomeController.*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("START: {}", joinPoint.toString());
        try {
            return joinPoint.proceed();
        } finally {
            log.info("END: {}", joinPoint.toString());
        }
    }
}
