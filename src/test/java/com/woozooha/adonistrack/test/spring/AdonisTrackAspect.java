package com.woozooha.adonistrack.test.spring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.woozooha.adonistrack.ProfileAspect;

@Aspect
@Component
public class AdonisTrackAspect extends ProfileAspect {

    @Pointcut("execution(* com.woozooha.adonistrack.test.spring.*Controller.*(..))")
    protected void profilePointcut() {
    }

    @Pointcut("execution(* *(..)) && within(com.woozooha.adonistrack.test.spring..*)")
    protected void executionPointcut() {
    }

}
