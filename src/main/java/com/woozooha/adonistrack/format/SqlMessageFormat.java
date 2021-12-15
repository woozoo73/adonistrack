package com.woozooha.adonistrack.format;

import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;

@Getter
@Setter
public class SqlMessageFormat implements SqlFormat {

    public SqlMessageFormat() {
    }

    public SqlMessageFormat(int maxLength) {
        this.maxLength = maxLength;
    }

    private int maxLength = -1;

    @Override
    public String format(String sql) {
        if (sql == null || sql.length() == 0) {
            return sql;
        }

        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Insert) {
                String message = "INSERT";
                Insert insert = (Insert) statement;
                Table table = insert.getTable();
                if (table != null) {
                    String tableName = table.getName();
                    if (tableName != null) {
                        message += " INTO " + tableName;
                    }
                    Alias alias = table.getAlias();
                    if (alias != null) {
                        String aliasName = alias.getName();
                        if (aliasName!= null) {
                            message += " " + aliasName;
                        }
                    }
                }
                message += " ~";

                return message;
            }
            if (statement instanceof Update) {
                String message = "UPDATE";
                Update update = (Update) statement;
                Table table = update.getTable();
                if (table != null) {
                    String tableName = table.getName();
                    if (tableName != null) {
                        message += " " + tableName;
                    }
                    Alias alias = table.getAlias();
                    if (alias != null) {
                        String aliasName = alias.getName();
                        if (aliasName!= null) {
                            message += " " + aliasName;
                        }
                    }
                }
                message += " SET ~";
                Expression where = update.getWhere();
                if (where != null) {
                    message += " WHERE " + where + " ~";
                }

                return message;
            }
            if (statement instanceof Delete) {
                String message = "DELETE";
                Delete delete = (Delete) statement;
                Table table = delete.getTable();
                if (table != null) {
                    String tableName = table.getName();
                    if (tableName != null) {
                        message += " FROM " + tableName;
                    }
                    Alias alias = table.getAlias();
                    if (alias != null) {
                        String aliasName = alias.getName();
                        if (aliasName!= null) {
                            message += " " + aliasName;
                        }
                    }
                }
                // message += " SET ~";
                Expression where = delete.getWhere();
                if (where != null) {
                    message += " WHERE " + where + " ~";
                }

                return message;
            }
            if (statement instanceof Select) {
                String message = "SELECT ~";
                Select select = (Select) statement;
                SelectBody selectBody = select.getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    FromItem fromItem = plainSelect.getFromItem();
                    if (fromItem instanceof Table) {
                        Table table = (Table) fromItem;
                        if (table != null) {
                            String tableName = table.getName();
                            if (tableName != null) {
                                message += " FROM " + tableName;
                            }
                        }
                        Alias alias = table.getAlias();
                        if (alias != null) {
                            String aliasName = alias.getName();
                            if (aliasName!= null) {
                                message += " " + aliasName;
                            }
                        }
                        if (table != null) {
                            message += " ~";
                        }
                        Expression where = plainSelect.getWhere();
                        if (where != null) {
                            message += " WHERE " + where + " ~";
                        }
                    }
                    Limit limit = plainSelect.getLimit();
                    if (limit != null) {
                        Expression offset = limit.getOffset();
                        Expression rowCount = limit.getRowCount();
                        message += " [";
                        if (offset != null) {
                            message += limit.getOffset() + ", ";
                        }
                        if (rowCount != null) {
                            message += limit.getRowCount();
                        }
                        message += "]";
                    }
                }

                return message;
            }
        } catch (Exception e) {
            //
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
