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

import com.woozooha.adonistrack.callback.WriterCallback;
import com.woozooha.adonistrack.conf.Config;
import com.woozooha.adonistrack.domain.*;
import com.woozooha.adonistrack.format.Format;
import com.woozooha.adonistrack.format.TextFormat;
import com.woozooha.adonistrack.writer.CompositeWriter;
import com.woozooha.adonistrack.writer.FileWriter;
import com.woozooha.adonistrack.writer.LogWriter;
import com.woozooha.adonistrack.writer.MemoryWriter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Predicate;

/**
 * Profile invocations aspect.
 *
 * @author woozoo73
 */
@Aspect
public abstract class ProfileAspect {

    protected static Config config;

    public ProfileAspect() {
        initConfig();
    }

    protected void initConfig() {
        try {
            config = new Config();

            CompositeWriter compositeWriter = new CompositeWriter();

            // default true.
            if (useLogWriter()) {
                LogWriter logWriter = makeLogWriter();
                if (logWriter != null) {
                    compositeWriter.add(logWriter);
                }
            }

            Predicate<Invocation> filter = getFilter();

            // default true.
            if (useMemoryWriter()) {
                int maxSize = getMemoryWriterMaxSize();
                MemoryWriter memoryWriter = makeMemoryWriter(maxSize, filter);
                if (memoryWriter != null) {
                    compositeWriter.add(memoryWriter);
                    config.setHistory(memoryWriter);
                }
            }

            // default false.
            if (useFileWriter()) {
                File dir = getFileWriterRootDir();
                FileWriter fileWriter = makeFileWriter(dir, filter);
                if (fileWriter != null) {
                    compositeWriter.add(fileWriter);
                    config.setHistory(fileWriter);
                }
            }

            WriterCallback invocationCallback = new WriterCallback();
            invocationCallback.setWriter(compositeWriter);

            config.setInvocationCallback(invocationCallback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected LogWriter makeLogWriter() {
        LogWriter logWriter = new LogWriter();
        Format format = new TextFormat("\n", null);
        logWriter.setFormat(format);

        return logWriter;
    }

    protected MemoryWriter makeMemoryWriter(int maxSize, Predicate<Invocation> filter) {
        MemoryWriter memoryWriter = new MemoryWriter();
        memoryWriter.setFilter(filter);
        memoryWriter.setMaxSize(maxSize);

        return memoryWriter;
    }

    protected FileWriter makeFileWriter(File dir, Predicate<Invocation> filter) {
        FileWriter fileWriter = new FileWriter();
        fileWriter.setRoot(dir);
        fileWriter.setFilter(filter);

        return fileWriter;
    }

    protected Predicate<Invocation> getFilter() {
        return (t) -> {
            if (t.getEventList() == null || t.getEventList().size() == 0) {
                return false;
            }
            Event event = t.getEventList().get(0);
            return event != null && event instanceof RequestEvent;
        };
    }

    protected int getMemoryWriterMaxSize() {
        return 100;
    }

    protected File getFileWriterRootDir() {
        return new File("/adonis-track");
    }

    protected boolean useLogWriter() {
        return true;
    }

    protected boolean useMemoryWriter() {
        return true;
    }

    protected boolean useFileWriter() {
        return false;
    }

    public static Config getConfig() {
        return config;
    }

    public static <V> Invocation before(JoinPoint joinPoint) {
        return before(Invocation.Type.Exec, joinPoint, null);
    }

    public static <V extends Call> Invocation before(Event<V> event) {
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
        Context current = Context.getCurrent().get();
        Invocation endpointInvocation = current.getEndpoint();

        Invocation invocation = new Invocation();
        invocation.setStartSeq(current.sequence());
        invocation.setType(type);

        if (joinPoint != null) {
            invocation.setJoinPoint(joinPoint);
            JoinPointInfo joinPointInfo = new JoinPointInfo(joinPoint);
            invocation.setJoinPointInfo(joinPointInfo);

            if (Context.isSourceLocation()) {
                SourceLocationInfo sourceLocation = joinPointInfo.getSourceLocation();
                int line = findSourceLine(joinPoint);
                sourceLocation.setLine(line);
            }
        }
        if (event != null) {
            invocation.add(event);
        }

        if (endpointInvocation == null) {
            current.setEndpoint(invocation);

            try {
                config.getInvocationCallback().before(invocation);
            } catch (Throwable throwable) {
            }
        }

        Invocation currentInvocation = current.peekFromInvocationStack();
        if (currentInvocation != null) {
            currentInvocation.add(invocation);
        }

        current.addToInvocationStack(invocation);

        invocation.start();

        return invocation;
    }

    protected static int findSourceLine(JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        if (target == null) {
            return 0;
        }

        Class<?> targetClass = target.getClass();
        if (targetClass == null) {
            return 0;
        }

        String targetClassName = targetClass.getName();
        if (targetClassName == null) {
            return 0;
        }

        StackTraceElement[] traces = new RuntimeException().getStackTrace();
        if (traces != null) {
            for (StackTraceElement trace : traces) {
                if (targetClassName.equals(trace.getClassName())) {
                    return trace.getLineNumber();
                }
                if (trace.getClassName().startsWith(targetClassName + "$$")) {
                    return trace.getLineNumber();
                }
            }
        }

        return 0;
    }

    private static Invocation after(JoinPoint joinPoint, Object r, Throwable t) {
        Invocation invocation = getInvocation(joinPoint);

        return after(invocation, r, t);
    }

    private static Invocation after(Invocation invocation, Object r, Throwable t) {
        Context current = Context.getCurrent().get();

        if (invocation == null) {
            return null;
        }

        invocation.setEndSeq(current.sequence());

        if (r != null) {
            invocation.setReturnValue(r);
            invocation.setReturnValueInfo(new ObjectInfo(r));
            if (r instanceof Event) {
                invocation.add((Event) r);
            }
        }
        if (t != null) {
            invocation.setT(t);
            invocation.setThrowableInfo(new ObjectInfo(t));
            addExceptionEvent(invocation, t);
        }

        invocation.stop();

        current.popFromInvocationStack();

        if (invocation.equalsJoinPoint(current.getEndpoint())) {
            Invocation i = current.dump();

            try {
                config.getInvocationCallback().after(i);
            } catch (Throwable throwable) {
            }
        }

        return invocation;
    }

    public static Invocation getInvocation(JoinPoint joinPoint) {
        Context current = Context.getCurrent().get();

        Invocation endpointInvocation = current.getEndpoint();
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

    @Around("executionPointcut()")
    public Object execution(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean globalTrace = Context.isGlobalTrace();
        boolean trace = false;
        Context current = null;
        if (globalTrace) {
            current = Context.getCurrent().get();
            if (current != null) {
                trace = current.checkTrace();
            }
        }

        Object r;

        try {
            if (trace) {
                before(joinPoint);
                current.increaseCount();
            }

            r = joinPoint.proceed();

            if (trace) {
                after(joinPoint, r);
            }
        } catch (Throwable t) {
            if (trace) {
                after(joinPoint, t);
            }

            throw t;
        }

        return r;
    }

    protected static void addExceptionEvent(Invocation invocation, Throwable t) {
        try {
            StringWriter stackTrace = new StringWriter();
            t.printStackTrace(new PrintWriter(stackTrace));

            ExceptionInfo exceptionInfo = new ExceptionInfo();
            exceptionInfo.setClassName(t.getClass().getName());
            exceptionInfo.setSimpleClassName(t.getClass().getSimpleName());
            exceptionInfo.setExceptionMessage(t.getMessage());
            exceptionInfo.setStackTrace(stackTrace.toString());

            ExceptionEvent exceptionEvent = new ExceptionEvent(exceptionInfo);
            invocation.add(exceptionEvent);
        } catch (Throwable t1) {
        }
    }

}
