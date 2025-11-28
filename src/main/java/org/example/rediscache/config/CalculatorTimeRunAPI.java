package org.example.rediscache.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Aspect
@Slf4j
public class CalculatorTimeRunAPI {

    @Around("execution(* org.example.rediscache.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Long start = Instant.now().toEpochMilli();
        Object proceed = joinPoint.proceed();
        Long end = Instant.now().toEpochMilli();
        log.info("Time run API " + joinPoint.getSignature() + " : " + (end - start) + " ms");
        return proceed;
    }

}
