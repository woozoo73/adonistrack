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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import com.woozooha.adonistrack.Invocation;
import com.woozooha.adonistrack.stats.Record;

public class StatisticsRecorder implements InvocationCallback {

	private static Map<Method, Record> recordMap = new HashMap<Method, Record>();

	public static Map<Method, Record> getRecordMap() {
		return recordMap;
	}

	@Override
	public void before(Invocation invocation) {
	}

	@Override
	public void after(Invocation invocation) {
		if (invocation == null) {
			return;
		}

		JoinPoint joinPoint = invocation.getJoinPoint();
		if (joinPoint == null) {
			return;
		}

		Signature signature = joinPoint.getSignature();
		if (signature == null) {
			return;
		}

		if (!(signature instanceof MethodSignature)) {
			return;
		}

		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();

		record(method, invocation);
	}

	public static String prettyPrint() {
		StringBuilder builder = new StringBuilder();
		builder.append("Statistics Records").append("\n");
		builder.append("------------------------------").append("\n");
		Set<Method> methods = recordMap.keySet();
		for (Method method : methods) {
			Record record = recordMap.get(method);
			builder.append(method.toString()).append(" : ");
			builder.append(record.prettyPrint()).append("\n");
		}
		builder.append("------------------------------").append("\n");

		return builder.toString();
	}

	protected void record(Method method, Invocation invocation) {
		Record record = recordMap.get(method);
		if (record == null) {
			record = new Record();
			recordMap.put(method, record);
		}

		record.record(invocation);
	}

}
