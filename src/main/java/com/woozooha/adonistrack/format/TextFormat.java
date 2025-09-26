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
package com.woozooha.adonistrack.format;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.woozooha.adonistrack.domain.*;
import com.woozooha.adonistrack.util.ToStringUtils;

/**
 * Text output format.
 * 
 * @author woozoo73
 */
public class TextFormat implements Format {

    private static NumberFormat timeFormat = new DecimalFormat("###,##0.00");

    private String prefix;

    private String suffix;

    public TextFormat() {
    }

    public TextFormat(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String format(Invocation invocation) {
        try {
            StringBuilder builder = new StringBuilder();

            if (prefix != null) {
                builder.append(prefix);
            }

            try {
                builder.append(formatInternal(invocation));
            } catch (Exception e) {
            }

            if (suffix != null) {
                builder.append(suffix);
            }

            return builder.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String formatInternal(Invocation invocation) {
        if (invocation == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        indent(builder, invocation.getDepth());

        builder.append("----> ");

        callInfo(builder, invocation);

        eventInfo(builder, invocation);

        if (invocation.getChildInvocationList() != null) {
            for (Invocation childInvocation : invocation.getChildInvocationList()) {
                builder.append("\n");
                builder.append(formatInternal(childInvocation));
            }
        }
        builder.append("\n");
        indent(builder, invocation.getDepth());
        if (invocation.getThrowableInfo() == null) {
            builder.append("<---- ");
            returnInfo(builder, invocation);
        } else {
            builder.append("<<<<< ");
            throwInfo(builder, invocation);
        }

        return builder.toString();
    }

    private void indent(StringBuilder builder, int depth) {
        for (int i = 0; i < depth; i++) {
            builder.append("    ");
        }
    }

    private void callInfo(StringBuilder builder, Invocation invocation) {
        if (invocation.getType() == Invocation.Type.Event) {
            builder.append(invocation.getEventList().get(0));
            builder.append(" ");
            durationInfo(builder, invocation);

            return;
        }

        JoinPointInfo joinPoint = invocation.getJoinPointInfo();
        SignatureInfo signature = joinPoint.getSignatureInfo();
        if (signature != null) {
            String targetName;
            if (joinPoint.getTarget().getProxyTargetName() != null) {
                targetName = joinPoint.getTarget().getProxyTargetName();
            } else {
                targetName = signature.getDeclaringTypeName();
            }
            builder.append(targetName);
            builder.append(".");
            builder.append(signature.getName());
            builder.append("(");
            argsInfo(builder, invocation);
            builder.append(")");
        }
        SourceLocationInfo sourceLocation = joinPoint.getSourceLocation();
        if (sourceLocation != null) {
            builder.append(" : Line ");
            builder.append(sourceLocation.getLine());
        }

        builder.append(" ");
        durationInfo(builder, invocation);
    }

    private void argsInfo(StringBuilder builder, Invocation invocation) {
        JoinPointInfo joinPoint = invocation.getJoinPointInfo();
        ObjectInfo[] argsInfo = joinPoint.getArgsInfo();
        if (argsInfo != null) {
            boolean first = true;
            for (ObjectInfo argInfo : argsInfo) {
                if (!first) {
                    builder.append(", ");
                }
                builder.append(argInfo.toString());
                first = false;
            }
        }
    }

    private void returnInfo(StringBuilder builder, Invocation invocation) {
        builder.append(invocation.getReturnValueInfo());
    }

    private void throwInfo(StringBuilder builder, Invocation invocation) {
        builder.append(invocation.getThrowableInfo());
    }

    private void durationInfo(StringBuilder builder, Invocation invocation) {
        builder.append("(");
        builder.append(
                invocation.getDuration() == null ? "0" : timeFormat.format(invocation.getDuration()));
        builder.append("ms");
        builder.append(":");
        builder.append(timeFormat.format(invocation.getDurationPercentage()));
        builder.append("%");
        builder.append(")");
    }

    private void eventInfo(StringBuilder builder, Invocation invocation) {
        if (invocation.getType() == Invocation.Type.Event) {
            return;
        }

        if (invocation.getEventList() == null || invocation.getEventList().isEmpty()) {
            return;
        }

        for (Event<?> event : invocation.getEventList()) {
            builder.append("\n");
            indentEvent(builder, invocation.getDepth());
            builder.append(ToStringUtils.format(event));
        }
    }

    private void indentEvent(StringBuilder builder, int depth) {
        indent(builder, depth);
        builder.append("      | ");
    }

}
