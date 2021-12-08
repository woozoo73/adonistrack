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
import java.lang.reflect.Proxy;

import com.woozooha.adonistrack.conf.Config;
import com.woozooha.adonistrack.util.ToStringUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

/**
 * Object data.
 * 
 * @author woozoo73
 */
@Data
public class ObjectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter(value = AccessLevel.NONE)
    private Class<?> declaringType;

    private String declaringTypeString;

    private String toStringValue;

    private boolean proxy;

    @Getter(value = AccessLevel.NONE)
    private Class<?> proxyTarget;

    private String proxyTargetString;

    private String proxyTargetName;

    public ObjectInfo() {
    }

    public ObjectInfo(Object object) {
        if (object == null) {
            return;
        }

        this.declaringType = object.getClass();
        if (this.declaringType != null) {
            this.declaringTypeString = this.declaringType.getName();
        }
        this.toStringValue = ToStringUtils.format(object);

        this.proxy = Proxy.isProxyClass(this.declaringType);
        if (this.proxy) {
            try {
                this.proxyTarget = Config.getProxyTargetFinder().apply(this.declaringType);
            } catch (Throwable t) {
                this.proxyTarget = this.declaringType;
            }
        }
        if (this.proxyTarget != null) {
            this.proxyTargetString = this.proxyTarget.getName();
        }
    }

    public String getProxyTargetName() {
        if (this.proxyTarget == null) {
            return null;
        }
        return this.proxyTarget.getName();
    }

    @Override
    public String toString() {
        return toStringValue;
    }

}
