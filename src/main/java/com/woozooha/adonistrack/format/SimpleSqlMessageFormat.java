package com.woozooha.adonistrack.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleSqlMessageFormat implements SqlFormat {

    private static final String CLEAN_REGEX = "(?s)/\\*.*?\\*/|--.*";

    private static final String COMMAND_REGEX = "(?i)^\\s*(SELECT|INSERT|UPDATE|DELETE|MERGE)";
    private static final Pattern COMMAND_PATTERN = Pattern.compile(COMMAND_REGEX);

    private static final String TABLE_FROM_REGEX = "(?i)FROM\\s+([^\\s(),;]+)";
    private static final Pattern TABLE_FROM_PATTERN = Pattern.compile(TABLE_FROM_REGEX);

    private static final String TABLE_INTO_REGEX = "(?i)INTO\\s+([^\\s(),;]+)";
    private static final Pattern TABLE_INTO_PATTERN = Pattern.compile(TABLE_INTO_REGEX);

    private static final String TABLE_UPDATE_REGEX = "(?i)UPDATE\\s+([^\\s(),;]+)";
    private static final Pattern TABLE_UPDATE_PATTERN = Pattern.compile(TABLE_UPDATE_REGEX);

    @Override
    public String format(String sql) {
        String cleanSql = clean(sql);

        String command = findCommand(cleanSql);
        String table = findTable(cleanSql, command);

        if (command == null) {
            command = "?";
        }
        if (table == null) {
            table = "?";
        }

        return command + " " + table;
    }

    protected String clean(String sql) {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }

        return sql.replaceAll(CLEAN_REGEX, " ").trim();
    }

    protected String findCommand(String sql) {
        if (sql == null || sql.isEmpty()) {
            return null;
        }

        Matcher commandMatcher = COMMAND_PATTERN.matcher(sql);
        if (!commandMatcher.find()) {
            return null;
        }

        return commandMatcher.group(1).toUpperCase();
    }

    protected String findTable(String sql, String command) {
        if (sql == null || sql.isEmpty()) {
            return null;
        }
        if (command == null) {
            return null;
        }

        Pattern tablePattern = null;
        switch (command) {
            case "SELECT":
            case "DELETE":
                tablePattern = TABLE_FROM_PATTERN;
                break;
            case "INSERT":
            case "MERGE":
                tablePattern = TABLE_INTO_PATTERN;
                break;
            case "UPDATE":
                tablePattern = TABLE_UPDATE_PATTERN;
                break;
            default:
                tablePattern = null;
                break;
        }
        if (tablePattern == null) {
            return null;
        }

        Matcher tableMatcher = tablePattern.matcher(sql);
        int tableCount = 0;
        String table = null;
        while (tableMatcher.find()) {
            if (tableCount > 0) {
                return null;
            }

            table = tableMatcher.group(1);
            tableCount++;
        }

        return cleanSchema(table);
    }

    protected String cleanSchema(String table) {
        if (table == null || table.isEmpty()) {
            return table;
        }

        int lastDotIndex = table.lastIndexOf('.');
        if (lastDotIndex < 0 || lastDotIndex == table.length() - 1) {
            return table;
        }

        return table.substring(lastDotIndex + 1);
    }

    @Override
    public int getMaxLength() {
        return 0;
    }

}
