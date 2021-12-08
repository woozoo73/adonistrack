package com.woozooha.adonistrack.domain;

import com.woozooha.adonistrack.aspect.SqlAspect;
import com.woozooha.adonistrack.format.SqlFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class SqlInfo implements Call, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer connectionId;

    private Type type;

    private String sql;

    private Long start;

    private Long end;

    private Long duration;

    private Map<Integer, ObjectInfo> parameterMap = new LinkedHashMap<>();

    private ObjectInfo throwableInfo;

    private String message;

    public String getMessage() {
        SqlFormat sqlFormat = SqlAspect.getSqlFormat();

        return sqlFormat.format(sql);
    }

    public void setParameter(Integer index, Object value) {
        if (parameterMap == null) {
            parameterMap = new TreeMap<Integer, ObjectInfo>();
        }

        parameterMap.put(index, new ObjectInfo(value));
    }

    public void calculateDuration() {
        if (start == null || end == null) {
            return;
        }

        duration = end - start;
    }

    public static enum Type {
        Statement,
        PreparedStatement,
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[id=");
        builder.append(id);
        builder.append(", sql=");
        builder.append(sql);
        if (parameterMap != null) {
            builder.append(", parameterMap=");
            builder.append(parameterMap);
        }
        if (throwableInfo != null) {
            builder.append(", throwableInfo=");
            builder.append(throwableInfo);
        }
        builder.append("]");
        return builder.toString();
    }

}
