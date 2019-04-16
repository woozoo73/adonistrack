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
package com.woozooha.adonistrack.callback;

import java.util.List;

import com.woozooha.adonistrack.Invocation;

/**
 * Composite invocation callback can holds many callbacks.
 * 
 * @author woozoo73
 */
public class CompositeCallback implements InvocationCallback {

	private List<InvocationCallback> callbacks;

	@Override
	public void before(Invocation invocation) {
		if (callbacks == null) {
			return;
		}

		for (InvocationCallback callback : callbacks) {
			callback.before(invocation);
		}
	}

	@Override
	public void after(Invocation invocation) {
		if (callbacks == null) {
			return;
		}

		for (InvocationCallback callback : callbacks) {
			callback.after(invocation);
		}
	}

	public List<InvocationCallback> getCallbacks() {
		return callbacks;
	}

	public void setCallbacks(List<InvocationCallback> callbacks) {
		this.callbacks = callbacks;
	}

}
