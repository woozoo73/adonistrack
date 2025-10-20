package com.woozooha.adonistrack.util;

public class SimpleToStringFormat implements ToStringFormat {

    public String format(Object object) {
        if (object == null) {
            return null;
        }

        String value = null;

        if (object instanceof byte[]) {
            value = new String((byte[]) object);
        }

        try {
            value = object.toString();
        } catch (Throwable t) {
            String clazz = object.getClass().getSimpleName();
            String throwable = t.getClass().getSimpleName();

            value = String.format("%s(FIXME: Can't use toString() because '%s')", clazz, throwable);
        }

        return value;
    }

}
