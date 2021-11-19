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

    @Getter(value = AccessLevel.NONE)
    private Long durationNanoTime;

    private Double durationPercentage = 100D;

    private Long start;

    private Long end;

    @Getter(value = AccessLevel.NONE)
    private Long startNanoTime;

    @Getter(value = AccessLevel.NONE)
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
        start = System.currentTimeMillis();
        startNanoTime = System.nanoTime();
    }

    public void stop() {
        end = System.currentTimeMillis();
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

    public Double getDuration() {
        if (durationNanoTime == null) {
            return null;
        }

        return durationNanoTime.doubleValue() / (1000 * 1000);
    }

    public enum Type {
        Event, Exec
    }

    @Getter
    @Setter
    public static class InvocationSummary {

        private String id;

        private Long start;

        private Double duration;

        private String method;

        private String requestURL;

        private String queryString;

        private Integer status;

        public InvocationSummary(Invocation invocation) {
            this.id = invocation.getId();
            this.start = invocation.getStart();
            this.duration = invocation.getDuration();

            RequestInfo req = null;
            ResponseInfo res = null;
            if (invocation.getEventList() != null) {
                for (Event event : invocation.getEventList()) {
                    if (req == null && event instanceof RequestInfoEvent) {
                        req = ((RequestInfoEvent) event).getValue();
                    }
                    if (event instanceof ResponseInfoEvent) {
                        res = ((ResponseInfoEvent) event).getValue();
                    }
                }
            }
            if (req != null) {
                this.method = req.getMethod();
                this.requestURL = req.getRequestURL();
                this.queryString = req.getQueryString();
            }
            if (res != null) {
                this.status = res.getStatus();
            }
        }
    }

}
