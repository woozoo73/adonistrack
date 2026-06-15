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
package com.woozooha.adonistrack.conf;

import java.io.Serializable;

import com.woozooha.adonistrack.callback.InvocationCallback;
import com.woozooha.adonistrack.fuction.Function;
import com.woozooha.adonistrack.writer.History;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration.
 * 
 * @author woozoo73
 */
@Data
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Function<Class<?>, Class<?>> DEFAULT_PROXY_TARGET_FINDER = new Function<Class<?>, Class<?>>() {
        public Class<?> apply(Class<?> proxyClass) {
            Class<?>[] interfaces = proxyClass.getInterfaces();
            for (Class<?> inf : interfaces) {
                return inf;
            }
            return proxyClass;
        }
    };

    protected InvocationCallback invocationCallback;

    protected History history;

    // Finder of Proxy class's real class(or interface).
    // $Proxy1 --> Foo
    @Getter
    @Setter
    protected static Function<Class<?>, Class<?>> proxyTargetFinder = DEFAULT_PROXY_TARGET_FINDER;

    @Data
    @AllArgsConstructor
    public static class BaseNameTargetFinder implements Function<Class<?>, Class<?>> {

        private String[] baseNames;

        public Class<?> apply(Class<?> proxyClass) {
            Class<?>[] interfaces = proxyClass.getInterfaces();
            for (Class<?> inf : interfaces) {
                for (String baseName : baseNames) {
                    if (inf.getName().startsWith(baseName)) {
                        return inf;
                    }
                }
            }
            return proxyClass;
        }

    }

}
