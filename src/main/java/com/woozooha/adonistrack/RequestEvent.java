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

import javax.servlet.http.HttpServletRequest;

public class RequestEvent extends Event {

    public RequestEvent() {
    }

    public RequestEvent(HttpServletRequest request) {
        super(request);
    }

    public String toString() {
        HttpServletRequest request = (HttpServletRequest) value;

        StringBuilder builder = new StringBuilder();
        builder.append(request.getMethod());
        builder.append(" ");
        builder.append(request.getRequestURL());
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }

        return builder.toString();
    }

}
