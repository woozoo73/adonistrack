package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionEvent extends Event<ExceptionInfo> {

    protected String type = "Exception";

    public ExceptionEvent() {
    }

    public ExceptionEvent(ExceptionInfo exceptionInfo) {
        super(exceptionInfo);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(type);
        builder.append("]");
        builder.append(" ");
        builder.append(value);
        return builder.toString();
    }

}
