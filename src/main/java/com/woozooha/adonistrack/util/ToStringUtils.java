package com.woozooha.adonistrack.util;

public abstract class ToStringUtils {

    private static ToStringFormat format = new SimpleToStringFormat();

    public static String format(Object object) {
        return format.format(object);
    }

}
