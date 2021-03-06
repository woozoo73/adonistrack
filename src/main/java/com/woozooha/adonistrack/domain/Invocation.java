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
package com.woozooha.adonistrack.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Invocation data.
 * 
 * @author woozoo73
 */
@Getter
@Setter
public class Invocation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id = UUID.randomUUID().toString();

    private Integer depth = 0;

    private Type type;

    private List<Invocation> childInvocationList;

    @Getter(value = AccessLevel.NONE)
    private JoinPoint joinPoint;

    private JoinPointInfo joinPointInfo;

    private Long durationNanoTime;

    private Double durationPercentage = 100D;

    private Long startNanoTime;

    private Long endNanoTime;

    @Getter(value = AccessLevel.NONE)
    private Object returnValue;

    private ObjectInfo returnValueInfo;

    @Getter(value = AccessLevel.NONE)
    private Throwable t;

    private ObjectInfo throwableInfo;

    private List<Event<?>> eventList;

    public boolean equalsJoinPoint(Invocation another) {
        if (another == null) {
            return false;
        }

        return equalsJoinPoint(another.joinPoint);
    }

    public boolean equalsJoinPoint(JoinPoint anotherJoinPoint) {
        if (joinPoint == null) {
            return anotherJoinPoint == null;
        }

        return joinPoint.equals(anotherJoinPoint);
    }

    public Invocation getInvocationByJoinPoint(JoinPoint search) {
        if (search == null) {
            return null;
        }

        if (equalsJoinPoint(search)) {
            return this;
        }

        if (childInvocationList != null) {
            for (Invocation childInvocation : childInvocationList) {
                Invocation match = childInvocation.getInvocationByJoinPoint(search);
                if (match != null) {
                    return match;
                }
            }
        }

        return null;
    }

    public void start() {
        startNanoTime = System.nanoTime();
    }

    public void stop() {
        endNanoTime = System.nanoTime();
        durationNanoTime = endNanoTime - startNanoTime;
    }

    public void add(Invocation childInvocation) {
        childInvocation.setDepth(depth + 1);

        if (childInvocationList == null) {
            childInvocationList = new ArrayList<Invocation>();
        }
        childInvocationList.add(childInvocation);
    }

    public void add(Event<?> event) {
        if (eventList == null) {
            eventList = new ArrayList<Event<?>>();
        }
        eventList.add(event);
    }

    public void calculateChildDurationPercentage() {
        Long totalSibling = 0L;

        if (childInvocationList != null) {
            for (Invocation invocation : childInvocationList) {
                if (invocation.durationNanoTime != null) {
                    totalSibling += invocation.durationNanoTime;
                }
            }
        }

        if (childInvocationList != null) {
            for (Invocation invocation : childInvocationList) {
                Double percentage = 0D;
                if (totalSibling > 0L) {
                    percentage = (100D * invocation.durationNanoTime) / totalSibling;
                }
                invocation.setDurationPercentage(percentage);

                invocation.calculateChildDurationPercentage();
            }
        }
    }

    public Double getDurationMiliTime() {
        if (durationNanoTime == null) {
            return null;
        }

        return durationNanoTime.doubleValue() / (1000 * 1000);
    }

    public enum Type {
        Event, Exec
    }

}
