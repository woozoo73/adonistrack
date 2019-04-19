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

import org.aspectj.lang.Signature;

/**
 * Signature data.
 * 
 * @author woozoo73
 */
public class SignatureInfo implements Signature, Serializable {

    private static final long serialVersionUID = 1L;

    private Class<?> declaringType;

    private String name;

    private int modifiers;

    private String shortString;

    private String longString;

    private String declaringTypeName;

    public SignatureInfo() {
    }

    public SignatureInfo(Signature signature) {
        if (signature == null) {
            return;
        }

        this.shortString = signature.toShortString();
        this.longString = signature.toLongString();
        this.name = signature.getName();
        this.modifiers = signature.getModifiers();
        this.declaringType = signature.getDeclaringType();
        this.declaringTypeName = signature.getDeclaringTypeName();
    }

    @Override
    public String toShortString() {
        return shortString;
    }

    @Override
    public String toLongString() {
        return longString;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    public void setDeclaringType(Class<?> declaringType) {
        this.declaringType = declaringType;
    }

    @Override
    public Class<?> getDeclaringType() {
        return declaringType;
    }

    public void setDeclaringTypeName(String declaringTypeName) {
        this.declaringTypeName = declaringTypeName;
    }

    @Override
    public String getDeclaringTypeName() {
        return declaringTypeName;
    }

}
