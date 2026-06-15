package com.woozooha.adonistrack.format;

import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;

import java.io.StringReader;

@Getter
@Setter
public class SqlMessageFormat implements SqlFormat {

    public SqlMessageFormat() {
        this(100);
    }

    public SqlMessageFormat(int maxLength) {
        this(maxLength, false);
    }

    public SqlMessageFormat(int maxLength, boolean includeWhere) {
        this.maxLength = maxLength;
        this.includeWhere = includeWhere;
    }

    private int maxLength = 100;
    private boolean includeWhere = false;

    public String format(String sql) {
        if (sql == null || sql.length() == 0) {
            return sql;
        }

        try {
            // 1. CCJSqlParserUtil.parse 대신 CCJSqlParserManager 인스턴스 사용
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            Statement statement = parserManager.parse(new StringReader(sql));

            // [INSERT 처리]
            if (statement instanceof Insert) {
                String message = "INSERT";
                Insert insert = (Insert) statement;
                Table table = insert.getTable();
                if (table != null) {
                    String tableName = table.getName();
                    if (tableName != null) {
                        message += " INTO " + tableName;
                    }
                    // 0.7.0에서 Alias는 별도 객체가 아니라 String입니다.
                    String aliasName = table.getAlias();
                    if (aliasName != null && aliasName.trim().length() > 0) {
                        message += " " + aliasName;
                    }
                }
                message += " ~";
                return message;
            }

            // [UPDATE 처리]
            if (statement instanceof Update) {
                String message = "UPDATE";
                Update update = (Update) statement;
                Table table = update.getTable();
                if (table != null) {
                    String tableName = table.getName();
                    if (tableName != null) {
                        message += " " + tableName;
                    }
                    String aliasName = table.getAlias();
                    if (aliasName != null && aliasName.trim().length() > 0) {
                        message += " " + aliasName;
                    }
                }
                message += " SET ~";
                if (includeWhere) {
                    Expression where = update.getWhere();
                    if (where != null) {
                        message += " WHERE " + where + " ~";
                    }
                }
                return message;
            }

            // [DELETE 처리]
            if (statement instanceof Delete) {
                String message = "DELETE";
                Delete delete = (Delete) statement;
                Table table = delete.getTable();
                if (table != null) {
                    String tableName = table.getName();
                    if (tableName != null) {
                        message += " FROM " + tableName;
                    }
                    String aliasName = table.getAlias();
                    if (aliasName != null && aliasName.trim().length() > 0) {
                        message += " " + aliasName;
                    }
                }
                if (includeWhere) {
                    Expression where = delete.getWhere();
                    if (where != null) {
                        message += " WHERE " + where + " ~";
                    }
                }
                return message;
            }

            // [SELECT 처리]
            if (statement instanceof Select) {
                String message = "SELECT ~";
                Select select = (Select) statement;
                SelectBody selectBody = select.getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    FromItem fromItem = plainSelect.getFromItem();
                    if (fromItem instanceof Table) {
                        Table table = (Table) fromItem;
                        String tableName = table.getName();
                        if (tableName != null) {
                            message += " FROM " + tableName;
                        }
                        String aliasName = table.getAlias();
                        if (aliasName != null && aliasName.trim().length() > 0) {
                            message += " " + aliasName;
                        }
                        message += " ~";
                        if (includeWhere) {
                            Expression where = plainSelect.getWhere();
                            if (where != null) {
                                message += " WHERE " + where + " ~";
                            }
                        }
                    }
                    Limit limit = plainSelect.getLimit();
                    if (limit != null) {
                        long offset = limit.getOffset();
                        long rowCount = limit.getRowCount();
                        boolean hasOffset = limit.isOffsetJdbcParameter();

                        message += " [";
                        if (hasOffset) {
                            message += offset + ", ";
                        }
                        message += rowCount;
                        message += "]";
                    }
                }
                return message;
            }
        } catch (Exception e) {
            // 예외 무시 및 cut(sql) 반환 유지
        }

        return cut(sql);
    }

    private String cut(String sql) {
        if (sql == null || sql.length() == 0) {
            return sql;
        }

        String oneLine = sql.replaceAll("\r", " ");
        oneLine = oneLine.replaceAll("\n", " ");

        if (maxLength < 0 || oneLine.length() <= maxLength) {
            return oneLine;
        }

        return oneLine.substring(0, maxLength) + " ...";
    }

}
