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

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Stack;

/**
 * Invocation context.
 *
 * @author woozoo73
 */
public class Context implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_TRACE_COUNT = 2000;

    private static ThreadLocal<Boolean> TRACE_CONTEXT = ThreadLocal.withInitial(() -> false);

    private static ThreadLocal<Integer> TRACE_COUNT = ThreadLocal.withInitial(() -> 0);

    private static ThreadLocal<Invocation> ENDPOINT_INVOCATION_CONTEXT = new ThreadLocal<>();

    private static ThreadLocal<Integer> ENDPOINT_INVOCATION_SEQ = ThreadLocal.withInitial(() -> 0);

    private static ThreadLocal<Stack<Invocation>> INVOCATION_STACK_CONTEXT = ThreadLocal.withInitial(() -> new Stack<>());

    @Setter
    @Getter
    private static int maxTraceCount = DEFAULT_TRACE_COUNT;

    @Getter
    @Setter
    private static boolean sourceLocation = false;

    public static boolean getTrace() {
        return TRACE_CONTEXT.get();
    }

    public static void setTrace(boolean traceContext) {
        TRACE_CONTEXT.set(traceContext);
    }

    public static Integer getTraceCount() {
        return TRACE_COUNT.get();
    }

    public static void setTraceCount(int count) {
        TRACE_COUNT.set(count);
    }

    public static void increaseTraceCount() {
        int count = TRACE_COUNT.get();
        TRACE_COUNT.set(count + 1);
    }

    public static boolean exceedMaxTraceCount() {
        return getTraceCount() >= maxTraceCount;
    }

    public static Invocation getEndpointInvocation() {
        return ENDPOINT_INVOCATION_CONTEXT.get();
    }

    public static void setEndpointInvocation(Invocation invocation) {
        ENDPOINT_INVOCATION_CONTEXT.set(invocation);
    }

    public static void addToInvocationStack(Invocation invocation) {
        Stack<Invocation> stack = INVOCATION_STACK_CONTEXT.get();
        stack.add(invocation);
    }

    public static int getInvocationStackSize() {
        return INVOCATION_STACK_CONTEXT.get().size();
    }

    public static Invocation popFromInvocationStack() {
        Stack<Invocation> stack = INVOCATION_STACK_CONTEXT.get();
        return stack.pop();
    }

    public static Invocation peekFromInvocationStack() {
        Stack<Invocation> stack = INVOCATION_STACK_CONTEXT.get();
        if (stack.isEmpty()) {
            return null;
        }

        return stack.peek();
    }

    public static Integer sequence() {
        Integer sequence = ENDPOINT_INVOCATION_SEQ.get();
        ENDPOINT_INVOCATION_SEQ.set(++sequence);

        return sequence;
    }

    public static Invocation dump() {
        return dump(true);
    }

    public static Invocation dump(boolean calculate) {
        Invocation invocation;

        try {
            invocation = ENDPOINT_INVOCATION_CONTEXT.get();
            if (invocation != null && calculate) {
                invocation.calculateChildDurationPercentage();
            }
        } finally {
            clear();
        }

        return invocation;
    }

    public static void clear() {
        TRACE_CONTEXT.remove();
        TRACE_COUNT.remove();
        ENDPOINT_INVOCATION_CONTEXT.remove();
        ENDPOINT_INVOCATION_SEQ.remove();
        INVOCATION_STACK_CONTEXT.remove();
    }

    public static boolean add(Event event) {
        Invocation invocation = Context.peekFromInvocationStack();

        if (invocation == null) {
            return false;
        }

        invocation.add(event);

        return true;
    }

}
