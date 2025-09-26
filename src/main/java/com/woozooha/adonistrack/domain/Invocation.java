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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Invocation data.
 * 
 * @author woozoo73
 */
@Getter
@Setter
public class Invocation implements Call, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String DATE_PATTERN = "yyyyMMdd";
    public static final String DATE_TIME_PATTERN = "yyyyMMdd-HHmmss-SSS";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    public static final int PSEUDO_UUID_LENGTH = 4;

    // yyyyMMdd-HHmmss-SSS-pseudouu
    // 20211206-184100-020-f26c
    public static final int ID_LENGTH = DATE_TIME_PATTERN.length() + 1 + PSEUDO_UUID_LENGTH;

    private String id = generateId();

    private Integer depth = 0;

    private Type type;

    private List<Invocation> childInvocationList;

    @Getter(value = AccessLevel.NONE)
    private JoinPoint joinPoint;

    private JoinPointInfo joinPointInfo;

    @Getter(value = AccessLevel.NONE)
    private Long durationNanoTime;

    private Double durationPercentage = 100D;

    private Integer startSeq;

    private Integer endSeq;

    private Long start;

    private Long end;

    private Double duration;

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

    private String generateId() {
        return String.format("%s-%s", DATE_TIME_FORMATTER.format(LocalDateTime.now()), UUID.randomUUID().toString().substring(0, PSEUDO_UUID_LENGTH));
    }

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
        duration = durationNanoTime.doubleValue() / (1000 * 1000);
    }

    public void add(Invocation childInvocation) {
        childInvocation.setDepth(depth + 1);

        if (childInvocationList == null) {
            childInvocationList = new ArrayList<>();
        }
        childInvocationList.add(childInvocation);
    }

    public void add(Event<?> event) {
        event.setSeq(Context.sequence());

        if (eventList == null) {
            eventList = new ArrayList<Event<?>>();
        }
        eventList.add(event);
    }

    public void calculateChildDurationPercentage() {
        long totalSibling = 0L;

        if (childInvocationList != null) {
            for (Invocation invocation : childInvocationList) {
                if (invocation.durationNanoTime != null) {
                    totalSibling += invocation.durationNanoTime;
                }
            }
        }

        if (childInvocationList != null) {
            for (Invocation invocation : childInvocationList) {
                double percentage = 0D;
                if (totalSibling > 0L) {
                    percentage = (100D * invocation.durationNanoTime) / totalSibling;
                }
                invocation.setDurationPercentage(percentage);

                invocation.calculateChildDurationPercentage();
            }
        }
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

        private String requestURI;

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
                    if (req == null && event instanceof RequestEvent) {
                        req = ((RequestEvent) event).getValue();
                    }
                    if (event instanceof ResponseEvent) {
                        res = ((ResponseEvent) event).getValue();
                    }
                }
            }
            if (req != null) {
                this.method = req.getMethod();
                this.requestURI = req.getRequestURI();
                this.queryString = req.getQueryString();
            }
            if (res != null) {
                this.status = res.getStatus();
            }
        }
    }

}
