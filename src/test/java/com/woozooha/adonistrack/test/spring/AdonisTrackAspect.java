package com.woozooha.adonistrack.test.spring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.woozooha.adonistrack.ProfileAspect;

@Aspect
@Component
public class AdonisTrackAspect extends ProfileAspect {

    /**
     * Fix this <code>@Pointcut</code> expression according to your situation. For
     * example, modify "com.woozooha.adonistrack.test.spring" to your application's
     * top-level package name "com.yourcompany.killerapp".
     */
    @Pointcut("execution(* *(..)) && (within(com.woozooha.adonistrack.test.spring..*) || within(com.woozooha.adonistrack.test.spring..*+))")
    protected void executionPointcut() {
    }

}
