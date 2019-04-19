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

import java.io.Serializable;

/**
 * Object data.
 * 
 * @author woozoo73
 */
public class ObjectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Class<?> declaringType;

    private String toStringValue;

    public ObjectInfo() {
    }

    public ObjectInfo(Object object) {
        if (object == null) {
            return;
        }

        this.declaringType = object.getClass();
        if (object instanceof byte[]) {
            // FIXME: Use ToString
            this.toStringValue = new String((byte[]) object);
        } else {
            try {
                // FIXME: Use ToString
                this.toStringValue = object.toString();
            } catch (Throwable t) {
            }
        }
    }

    public Class<?> getDeclaringType() {
        return declaringType;
    }

    public void setDeclaringType(Class<?> declaringType) {
        this.declaringType = declaringType;
    }

    public String getToStringValue() {
        return toStringValue;
    }

    public void setToStringValue(String toStringValue) {
        this.toStringValue = toStringValue;
    }

    @Override
    public String toString() {
        return toStringValue;
    }

}
