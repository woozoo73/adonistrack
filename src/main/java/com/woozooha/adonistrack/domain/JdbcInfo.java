package com.woozooha.adonistrack.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JdbcInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<JdbcStatementInfo> statements;

    public void add(JdbcStatementInfo statement) {
        if (statement == null) {
            return;
        }

        if (statements == null) {
            statements = new ArrayList<JdbcStatementInfo>();
        }

        statements.add(statement);
    }

    public List<JdbcStatementInfo> getStatements() {
        return statements;
    }

    public void setStatements(List<JdbcStatementInfo> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JdbcInfo [statements=");
        builder.append(statements);
        builder.append("]");
        return builder.toString();
    }

}
