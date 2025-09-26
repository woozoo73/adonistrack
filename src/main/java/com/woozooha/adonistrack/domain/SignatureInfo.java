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
import org.aspectj.lang.Signature;

import lombok.Data;

/**
 * Signature data.
 * 
 * @author woozoo73
 */
@Data
public class SignatureInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter(value = AccessLevel.NONE)
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

	public String toShortString() {
		return shortString;
	}

	public String toLongString() {
		return longString;
	}

}
