package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JdbcEvent extends Event<JdbcStatementInfo> {

    protected String type = "JDBC";

    public JdbcEvent() {
    }

    public JdbcEvent(JdbcStatementInfo jdbcStatementInfo) {
        super(jdbcStatementInfo);
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
