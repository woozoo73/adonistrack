package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqlEvent extends Event<SqlInfo> {

    protected String type = "SQL";

    public SqlEvent() {
    }

    public SqlEvent(SqlInfo sqlInfo) {
        super(sqlInfo);
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
