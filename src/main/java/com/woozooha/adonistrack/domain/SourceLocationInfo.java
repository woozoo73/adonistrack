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

import lombok.AccessLevel;
import lombok.Getter;
import org.aspectj.lang.reflect.SourceLocation;

import lombok.Data;

/**
 * SourceLocation data.
 * 
 * @author woozoo73
 */
@Data
public class SourceLocationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter(value = AccessLevel.NONE)
    private Class<?> withinType;

    private String fileName;

    private int line;

    private int column;

    public SourceLocationInfo() {
    }

    public SourceLocationInfo(SourceLocation sourceLocation) {
        if (sourceLocation == null) {
            return;
        }

        this.withinType = sourceLocation.getWithinType();
    }

}
