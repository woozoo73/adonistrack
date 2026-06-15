package com.woozooha.adonistrack.util;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleToStringFormatTest {

    SimpleToStringFormat format = new SimpleToStringFormat();

    @Test
    public void testFormatNull() {
        String value = format.format(null);

        assertNull(value);
    }

    @Test
    public void testFormatString() {
        String string = "foo";
        String value = format.format(string);

        assertEquals("foo", value);
    }

    @Test
    public void testFormatStringNewline() {
        String string = "foo\nbar";
        String value = format.format(string);

        assertEquals("foo\nbar", value);
    }

    @Test
    public void testFormatBytes() throws UnsupportedEncodingException {
        byte[] bytes = "foo".getBytes();
        String value = format.format(bytes);

        assertNotNull(value);
    }

    @Test
    public void testFormatPlain() {
        PlainObject plain = new PlainObject();
        plain.setName("plain");
        String value = format.format(plain);

        assertNotNull(value);
    }

    /**
     * Safety check for StackOverflowError.
     */
    @Test
    public void testFormatRecursive() {
        RecursiveObject parent = new RecursiveObject();
        parent.setName("parent");

        RecursiveObject child = new RecursiveObject();
        child.setName("child");

        child.setParent(parent);
        parent.setChild(child);

        String parentValue = format.format(parent);
        String childValue = format.format(child);

        log.debug("parent: {}", parentValue);
        log.debug("child: {}", childValue);

        assertNotNull(parentValue);
        assertNotNull(childValue);
    }

    @Data
    private static class PlainObject {

        private String name;

    }

    @Data
    private static class RecursiveObject {

        private String name;

        private RecursiveObject parent;

        private RecursiveObject child;

    };

}
