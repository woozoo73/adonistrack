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
package com.woozooha.adonistrack.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import com.woozooha.adonistrack.callback.WriterCallback;
import com.woozooha.adonistrack.conf.Config;
import com.woozooha.adonistrack.domain.Context;
import com.woozooha.adonistrack.domain.Event;
import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.domain.JoinPointInfo;
import com.woozooha.adonistrack.domain.ObjectInfo;
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

    private static Config config;

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

    public static Config getConfig() {
        return config;
    }

    public static <V> Invocation before(JoinPoint joinPoint) {
        return before(Invocation.Type.Exec, joinPoint, null);
    }

    public static <V> Invocation before(Event<V> event) {
        return before(Invocation.Type.Event, null, event);
    }

    public static Invocation after(JoinPoint joinPoint, Object r) {
        return after(joinPoint, r, null);
    }

    public static Invocation after(JoinPoint joinPoint, Throwable t) {
        return after(joinPoint, null, t);
    }

    public static Invocation after(Invocation invocation, Object r) {
        return after(invocation, r, null);
    }

    public static Invocation after(Invocation invocation, Throwable t) {
        return after(invocation, null, t);
    }

    private static <V> Invocation before(Invocation.Type type, JoinPoint joinPoint, Event<V> event) {
        Invocation endpointInvocation = Context.getEndpointInvocation();

        Invocation invocation = new Invocation();
        invocation.setType(type);

        if (joinPoint != null) {
            invocation.setJoinPoint(joinPoint);
            invocation.setJoinPointInfo(new JoinPointInfo(joinPoint));
        }
        if (event != null) {
            invocation.add(event);
        }

        if (endpointInvocation == null) {
            Context.setEndpointInvocation(invocation);

            try {
                config.getInvocationCallback().before(invocation);
            } catch (Throwable throwable) {
            }
        }

        Invocation currentInvocation = Context.peekFromInvocationStack();
        if (currentInvocation != null) {
            currentInvocation.add(invocation);
        }

        Context.addToInvocationStack(invocation);

        invocation.start();

        return invocation;
    }

    private static Invocation after(JoinPoint joinPoint, Object r, Throwable t) {
        Invocation invocation = getInvocation(joinPoint);

        return after(invocation, r, t);
    }

    private static Invocation after(Invocation invocation, Object r, Throwable t) {
        if (invocation == null) {
            return null;
        }

        if (r != null) {
            invocation.setReturnValue(r);
            invocation.setReturnValueInfo(new ObjectInfo(r));
        }
        if (t != null) {
            invocation.setT(t);
            invocation.setThrowableInfo(new ObjectInfo(t));
        }

        invocation.stop();

        Context.popFromInvocationStack();

        if (invocation.equalsJoinPoint(Context.getEndpointInvocation())) {
            Invocation i = Context.dump();

            try {
                config.getInvocationCallback().after(i);
            } catch (Throwable throwable) {
            }
        }

        return invocation;
    }

    public static Invocation getInvocation(JoinPoint joinPoint) {
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

    @Pointcut
    public abstract void executionPointcut();

    @Before("executionPointcut()")
    public void executionBefore(JoinPoint joinPoint) {
        before(joinPoint);
    }

    @AfterThrowing(pointcut = "executionPointcut()", throwing = "t")
    public void profileAfterThrowing(JoinPoint joinPoint, Throwable t) {
        after(joinPoint, t);
    }

    @AfterReturning(pointcut = "executionPointcut()", returning = "r")
    public void profileAfterReturning(JoinPoint joinPoint, Object r) {
        after(joinPoint, r);
    }

}
