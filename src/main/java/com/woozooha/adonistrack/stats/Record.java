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
package com.woozooha.adonistrack.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.woozooha.adonistrack.domain.Invocation;

public class Record {

    private static NumberFormat timeFormat = new DecimalFormat("###,##0.00");

    private int requestCount;

    private int successCount;

    private int errorCount;

    private Double quickestDuration;

    private Double slowestDuration;

    private Double totalDuration;

    private Invocation slowestInvocation;

    public void record(Invocation invocation) {
        Double duration = invocation.getDuration();
        if (duration == null) {
            return;
        }

        requestCount++;
        if (invocation.getThrowableInfo() == null) {
            successCount++;

            if (totalDuration == null) {
                totalDuration = 0D;
            }
            totalDuration += duration;
            if (quickestDuration == null || duration < quickestDuration) {
                quickestDuration = duration;
            }
            if (slowestDuration == null || duration > slowestDuration) {
                slowestDuration = duration;
            }
            if (slowestInvocation == null
                    || (slowestDuration == null || duration > slowestDuration)) {
                slowestInvocation = invocation;
            }
        } else {
            errorCount++;
        }
    }

    public double getAverageDuration() {
        if (successCount == 0) {
            return 0;
        }

        return totalDuration / successCount;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public double getQuickestDuration() {
        return quickestDuration;
    }

    public double getSlowestDuration() {
        return slowestDuration;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public Invocation getSlowestInvocation() {
        return slowestInvocation;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Record [requestCount=");
        builder.append(requestCount);
        builder.append(", successCount=");
        builder.append(successCount);
        builder.append(", errorCount=");
        builder.append(errorCount);
        builder.append(", quickestDuration=");
        builder.append(quickestDuration);
        builder.append(", slowestDuration=");
        builder.append(slowestDuration);
        builder.append(", totalDuration=");
        builder.append(totalDuration);
        builder.append(", slowestInvocation=");
        builder.append(slowestInvocation);
        builder.append("]");
        return builder.toString();
    }

    public Object prettyPrint() {
        StringBuilder builder = new StringBuilder();
        builder.append("total=").append(requestCount).append(", ");
        builder.append("success=").append(successCount).append(", ");
        builder.append("error=").append(errorCount).append(", ");
        double min = 0D;
        if (quickestDuration != null) {
            min = quickestDuration.doubleValue() / (1000 * 1000);
        }
        double max = 0D;
        if (slowestDuration != null) {
            max = slowestDuration.doubleValue() / (1000 * 1000);
        }
        builder.append("min=").append(timeFormat.format(min)).append("(ms)").append(", ");
        builder.append("max=").append(timeFormat.format(max)).append("(ms)");

        return builder.toString();
    }

}
