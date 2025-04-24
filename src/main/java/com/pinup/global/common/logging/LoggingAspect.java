package com.pinup.global.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    // controller와 service method만 로그 적용
    @Around("(execution(* com.pinup.domain..service..*(..)))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("START: {}", joinPoint.getSignature().getName());
        try {
            return joinPoint.proceed();
        } finally {
            log.info("END: {}", joinPoint.getSignature().getName());
        }
    }

//    @Before("execution(* com.pinup.domain..service..*(..)) && !execution(* com.pinup.domain.home.HomeController.*(..))")
//    public void logBeforeMethod(JoinPoint joinPoint) {
//        log.info("START: {}", joinPoint.getSignature().getName());
//    }
//
//    @After("execution(* com.pinup.domain..service..*(..)) && !execution(* com.pinup.domain.home.HomeController.*(..))")
//    public void logAfterMethod(JoinPoint joinPoint) {
//        log.info("END: {}", joinPoint.getSignature().getName());
//    }
//
//    @AfterThrowing(pointcut = "execution(* com.pinup.domain..service..*(..)) && !execution(* com.pinup.domain.home.HomeController.*(..))", throwing = "ex")
//    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
//        log.error("Exception in Method: {}", joinPoint.getSignature().getName(), ex);
//    }
}
