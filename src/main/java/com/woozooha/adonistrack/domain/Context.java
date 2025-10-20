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
@Getter
@Setter
public class Context implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_TRACE_COUNT = 2000;

    @Getter
    protected static final ThreadLocal<Context> current = ThreadLocal.withInitial(() -> new Context());

    @Getter
    @Setter
    private static boolean globalTrace = true;

    @Getter
    @Setter
    private static int maxTraceCount = DEFAULT_TRACE_COUNT;

    @Getter
    @Setter
    private static boolean sourceLocation = false;

    private boolean trace = false;
    private int count = 0;
    private Invocation endpoint;
    private int seq = 0;
    private Stack<Invocation> stack = new Stack<>();

    public static boolean checkCurrentTrace() {
        if (!globalTrace) {
            return false;
        }
        Context context = current.get();
        if (context == null) {
            return false;
        }
        return context.checkTrace();
    }

    public boolean checkTrace() {
        if (!globalTrace) {
            return false;
        }
        return trace && getCount() < Context.getMaxTraceCount();
    }

    public int increaseCount() {
        return ++count;
    }

    public void addToInvocationStack(Invocation invocation) {
        stack.add(invocation);
    }

    public Invocation popFromInvocationStack() {
        return stack.pop();
    }

    public Invocation peekFromInvocationStack() {
        if (stack.isEmpty()) {
            return null;
        }

        return stack.peek();
    }

    public Integer sequence() {
        int sequence = getSeq();
        seq = ++sequence;

        return sequence;
    }

    public Invocation dump() {
        return dump(true);
    }

    public Invocation dump(boolean calculate) {
        Invocation invocation;

        try {
            invocation = getEndpoint();
            if (invocation != null && calculate) {
                invocation.calculateChildDurationPercentage();
            }
        } finally {
            clear();
        }

        return invocation;
    }

    public static void clear() {
        current.remove();
    }

}
