package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionInfo {

    private String className;

    private String simpleClassName;

    private String exceptionMessage;

    private String stackTrace;

    // AdonisTrack UI

    protected String message;

    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("[Exception]").append(" ").append(simpleClassName).append(" ");
        if (exceptionMessage != null) {
            if (exceptionMessage.length() > 20) {
                builder.append(exceptionMessage.substring(0, 20)).append("...");
            } else {
                builder.append(exceptionMessage);
            }
        }

        return builder.toString();
    }

}
