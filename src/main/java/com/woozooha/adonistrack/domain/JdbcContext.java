package com.woozooha.adonistrack.domain;

import java.io.Serializable;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class JdbcContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private static ThreadLocal<Map<Statement, JdbcStatementInfo>> STATEMENT_MAP = new ThreadLocal<Map<Statement, JdbcStatementInfo>>() {
        @Override
        protected Map<Statement, JdbcStatementInfo> initialValue() {
            return new HashMap<Statement, JdbcStatementInfo>();
        }
    };

    public static void put(Statement statement, JdbcStatementInfo statementInfo) {
        STATEMENT_MAP.get().put(statement, statementInfo);
    }

    public static JdbcStatementInfo get(Statement statement) {
        return STATEMENT_MAP.get().get(statement);
    }

}
