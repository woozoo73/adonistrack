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
package com.woozooha.adonistrack.test.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.woozooha.adonistrack.aspect.ProfileAspect;
import com.woozooha.adonistrack.domain.Event;
import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.domain.RequestInfo;
import com.woozooha.adonistrack.domain.RequestInfoEvent;
import com.woozooha.adonistrack.domain.ResponseInfo;
import com.woozooha.adonistrack.domain.ResponseInfoEvent;

public class AdonistrackInterceptor implements HandlerInterceptor {

    private static ThreadLocal<Invocation> CONTEXT = new ThreadLocal<Invocation>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Invocation invocation = CONTEXT.get();
        if (invocation == null) {
            invocation = before(request);
            CONTEXT.set(invocation);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {
        Invocation invocation = CONTEXT.get();
        after(invocation, request, response);
    }

    private Invocation before(HttpServletRequest request) {
        Event<RequestInfo> event = new RequestInfoEvent(new RequestInfo(request));

        return ProfileAspect.before(event);
    }

    private void after(Invocation invocation, HttpServletRequest request, HttpServletResponse response) {
        Event<ResponseInfo> event = new ResponseInfoEvent(new ResponseInfo(response));

        ProfileAspect.after(invocation, event);
    }

}
