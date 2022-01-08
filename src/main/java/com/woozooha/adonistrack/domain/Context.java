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
import java.util.Stack;

/**
 * Invocation context.
 * 
 * @author woozoo73
 */
public class Context implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ThreadLocal<Invocation> ENDPOINT_INVOCATION_CONTEXT = new ThreadLocal<Invocation>();

    private static ThreadLocal<Integer> ENDPOINT_INVOCATION_SEQ = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    private static ThreadLocal<Stack<Invocation>> INVOCATION_STACK_CONTEXT = new ThreadLocal<Stack<Invocation>>() {
        @Override
        protected Stack<Invocation> initialValue() {
            return new Stack<Invocation>();
        }
    };

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
        Invocation invocation = ENDPOINT_INVOCATION_CONTEXT.get();
        invocation.calculateChildDurationPercentage();

        ENDPOINT_INVOCATION_CONTEXT.set(null);
        ENDPOINT_INVOCATION_SEQ.set(0);
        INVOCATION_STACK_CONTEXT.set(new Stack<Invocation>());

        return invocation;
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
