package com.woozooha.adonistrack.util;

public abstract class StringUtils {

    public static String abbreviate(String source, int maxLength) {
        if (source == null || source.length() == 0 || source.length() <= maxLength) {
            return source;
        }

        return source.substring(0, maxLength) + "...";
    }

    public static String capitalize(String source) {
        if (source == null || source.length() == 0) {
            return source;
        }

        String first = source.substring(0, 1).toUpperCase();
        if (source.length() == 1) {
            return first;
        }

        return first + source.substring(1);
    }

}
