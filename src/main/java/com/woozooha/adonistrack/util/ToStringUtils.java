package com.woozooha.adonistrack.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ToStringUtils {

    private static final Log logger = LogFactory.getLog(ToStringUtils.class);

    private static ToStringFormat format;

    static {
        String toStringFormatClassName = System.getProperty("adonistrack.to-string.class");
        if (toStringFormatClassName == null) {
            format = new SimpleToStringFormat();
            logger.info("Set the ToStringFormat as default: " + format);
        } else {
            try {
                Class<?> toStringFormatClass = ToStringUtils.class.getClassLoader().loadClass(toStringFormatClassName);
                format = (ToStringFormat) toStringFormatClass.newInstance();
                logger.info("Set the ToStringFormat: " + format);
            } catch (Exception e) {
                logger.warn("Can't set the ToStringFormat class: '" + toStringFormatClassName + "'. so it was set as the default: " + format + ".", e);
            }
        }
    };

    public static String format(Object object) {
        return format.format(object);
    }

}
