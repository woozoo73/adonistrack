package com.woozooha.adonistrack.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilsTest {

    @Test
    public void capitalize() {
        assertNull(StringUtils.capitalize(null));
        assertEquals("", StringUtils.capitalize(""));
        assertEquals("I", StringUtils.capitalize("i"));
        assertEquals("I", StringUtils.capitalize("I"));
        assertEquals("My", StringUtils.capitalize("my"));
        assertEquals("My", StringUtils.capitalize("My"));
        assertEquals("MyName", StringUtils.capitalize("myName"));
        assertEquals("YourName", StringUtils.capitalize("YourName"));
    }

    @Test
    public void abbreviate() {
        assertNull(StringUtils.abbreviate(null, 100));
        assertEquals("", StringUtils.abbreviate("", 100));
        assertEquals("m", StringUtils.abbreviate("m", 100));
        assertEquals("my", StringUtils.abbreviate("my", 100));
        assertEquals("my name is", StringUtils.abbreviate("my name is", 100));
        assertEquals("my na" + "...", StringUtils.abbreviate("my name is", 5));
    }

}