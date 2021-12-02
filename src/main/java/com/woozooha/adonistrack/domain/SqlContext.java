package com.woozooha.adonistrack.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SqlContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final ThreadLocal<Map<Integer, SqlInfo>> SQL_MAP = new ThreadLocal<Map<Integer, SqlInfo>>() {
        @Override
        protected HashMap<Integer, SqlInfo> initialValue() {
            return new HashMap<>();
        }
    };

    public static void put(Integer id, SqlInfo sqlInfo) {
        SQL_MAP.get().put(id, sqlInfo);
    }

    public static SqlInfo get(Integer id) {
        return SQL_MAP.get().get(id);
    }

}
