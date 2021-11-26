package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
public class SqlInfo implements Call, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer connecitonId;

    private String sql;

    private Long start;

    private Long end;

    private Long duration;

    private Map<Integer, ObjectInfo> parameterMap;

    private ObjectInfo throwableInfo;

    public String getMessage() {
        if (sql != null) {
            try {
                Statement statement = CCJSqlParserUtil.parse(sql);
                if (statement instanceof Insert) {
                    Insert insert = (Insert) statement;
                    String tableName = insert.getTable().getName();
                    return String.format("INSERT ~ INTO %s", tableName);
                }
                if (statement instanceof Update) {
                    Update update = (Update) statement;
                    String tableName = update.getTable().getName();
                    return String.format("UPDATE %s ~", tableName);
                }
                if (statement instanceof Delete) {
                    Delete delete = (Delete) statement;
                    String tableName = delete.getTable().getName();
                    return String.format("DELETE %s ~", tableName);
                }
                if (statement instanceof Select) {
                    Select select = (Select) statement;
                    SelectBody selectBody = select.getSelectBody();
                    if (selectBody instanceof PlainSelect) {
                        PlainSelect plainSelect = (PlainSelect) selectBody;
                        FromItem fromItem = plainSelect.getFromItem();
                        if (fromItem instanceof Table) {
                            Table table = (Table) fromItem;
                            String tableName = table.getName();
                            return String.format("SELECT ~ FROM %s ~", tableName);
                        }
                    }
                }
            } catch (Exception e) {
                //
            }
        }
        return sql;
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
