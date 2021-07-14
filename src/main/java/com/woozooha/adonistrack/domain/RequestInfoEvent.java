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

@Getter
@Setter
public class RequestInfoEvent extends Event<RequestInfo> {

    protected String type = "REQUEST";

    public RequestInfoEvent() {
    }

    public RequestInfoEvent(RequestInfo requestInfo) {
        super(requestInfo);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(type);
        builder.append("]");
        builder.append(" ");
        builder.append(value.getMethod());
        builder.append(" ");
        builder.append(value.getRequestURL());
        if (value.getQueryString() != null) {
            builder.append("?");
            builder.append(value.getQueryString());
        }

        return builder.toString();
    }

}
