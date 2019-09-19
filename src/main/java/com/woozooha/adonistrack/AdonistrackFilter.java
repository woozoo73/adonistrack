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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdonistrackFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);

            return;
        }

        chain.doFilter(request, response);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Invocation invocation = before(request);

        try {
            chain.doFilter(request, response);
        } finally {
            after(invocation, request, response);
        }
    }

    private Invocation before(HttpServletRequest request) {
        Invocation endpointInvocation = Context.getEndpointInvocation();

        Invocation invocation = new Invocation();
        invocation.setType(Invocation.Type.HttpRequest);
        invocation.add(new RequestEvent(request));

        if (endpointInvocation == null) {
            Context.setEndpointInvocation(invocation);

            try {
                ProfileAspect.getConfig().getInvocationCallback().before(invocation);
            } catch (Throwable t) {
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

    private void after(Invocation invocation, HttpServletRequest request, HttpServletResponse response) {
        invocation.stop();

        Event responseEvent = new ResponseEvent(response);
        invocation.setReturnValue(responseEvent);
        invocation.setReturnValueInfo(new ObjectInfo(responseEvent));

        Context.popFromInvocationStack();

        if (invocation.equalsJoinPoint(Context.getEndpointInvocation())) {
            Invocation i = Context.dump();

            try {
                ProfileAspect.getConfig().getInvocationCallback().after(i);
            } catch (Throwable t) {
            }
        }
    }

}
