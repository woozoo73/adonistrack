package com.woozooha.adonistrack.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class SpyLogDelegatorAspect {

    @Pointcut("within(net.sf.log4jdbc.log.SpyLogDelegator+) && execution(boolean isJdbcLoggingEnabled())")
    public void isJdbcLoggingEnabledPointcut() {
    }

    @Around("isJdbcLoggingEnabledPointcut()")
    public Object forceJdbcLoggingEnabled(ProceedingJoinPoint joinPoint) {
        return true;
    }

}
