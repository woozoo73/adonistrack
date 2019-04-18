/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.woozooha.adonistrack;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.woozooha.adonistrack.callback.WriterCallback;
import com.woozooha.adonistrack.conf.Config;
import com.woozooha.adonistrack.format.Format;
import com.woozooha.adonistrack.format.TextFormat;
import com.woozooha.adonistrack.writer.LogWriter;
import com.woozooha.adonistrack.writer.Writer;

/**
 * Profile invocations aspect.
 * 
 * @author woozoo73
 */
@Aspect
public abstract class ProfileAspect {

    private Config config;

    public ProfileAspect() {
        initConfig();
    }

    protected void initConfig() {
        try {
            config = new Config();

            Format format = new TextFormat("\n", null);
            Writer writer = new LogWriter();
            writer.setFormat(format);

            WriterCallback invocationCallback = new WriterCallback();
            invocationCallback.setWriter(writer);

            config.setInvocationCallback(invocationCallback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void profilePointcut();

    protected abstract void executionPointcut();

    @Before("profilePointcut()")
    public void profileBefore(JoinPoint joinPoint) {
        Invocation endpointInvocation = Context.getEndpointInvocation();

        Invocation invocation = new Invocation();
        invocation.setJoinPoint(joinPoint);
        invocation.setJoinPointInfo(new JoinPointInfo(joinPoint));

        if (endpointInvocation == null) {
            Context.setEndpointInvocation(invocation);

            try {
                config.getInvocationCallback().before(invocation);
            } catch (Throwable t) {
            }
        }

        Invocation currentInvocation = Context.peekFromInvocationStack();
        if (currentInvocation != null) {
            currentInvocation.add(invocation);
        }

        Context.addToInvocationStack(invocation);

        invocation.start();
    }

    @Before("executionPointcut()")
    public void executionBefore(JoinPoint joinPoint) {
        Invocation endpointInvocation = Context.getEndpointInvocation();

        if (endpointInvocation == null) {
            return;
        }

        Invocation invocation = new Invocation();
        invocation.setJoinPoint(joinPoint);
        invocation.setJoinPointInfo(new JoinPointInfo(joinPoint));

        Invocation currentInvocation = Context.peekFromInvocationStack();
        if (currentInvocation != null) {
            currentInvocation.add(invocation);
        }

        Context.addToInvocationStack(invocation);

        invocation.start();
    }

    @AfterThrowing(pointcut = "executionPointcut()", throwing = "t")
    public void profileAfterThrowing(JoinPoint joinPoint, Throwable t) {
        Invocation invocation = getInvocation(joinPoint);
        if (invocation == null) {
            return;
        }

        invocation.setT(t);
        invocation.setThrowableInfo(new ObjectInfo(t));

        afterProfile(invocation);
    }

    @AfterReturning(pointcut = "executionPointcut()", returning = "r")
    public void profileAfterReturning(JoinPoint joinPoint, Object r) {
        Invocation invocation = getInvocation(joinPoint);
        if (invocation == null) {
            return;
        }

        invocation.setReturnValue(r);
        invocation.setReturnValueInfo(new ObjectInfo(r));

        afterProfile(invocation);
    }

    private Invocation getInvocation(JoinPoint joinPoint) {
        Invocation endpointInvocation = Context.getEndpointInvocation();
        if (endpointInvocation == null) {
            return null;
        }

        Invocation invocation = endpointInvocation.getInvocationByJoinPoint(joinPoint);
        if (invocation == null) {
            return null;
        }

        return invocation;
    }

    private void afterProfile(Invocation invocation) {
        invocation.stop();

        Context.popFromInvocationStack();

        if (invocation.equalsJoinPoint(Context.getEndpointInvocation())) {
            Invocation i = Context.dump();

            try {
                config.getInvocationCallback().after(i);
            } catch (Throwable t) {
            }
        }
    }

}
