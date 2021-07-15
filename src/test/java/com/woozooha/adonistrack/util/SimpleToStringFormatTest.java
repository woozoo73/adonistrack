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
    public void testFormtNull() {
        Object object = null;

        String value = format.format(object);

        log.debug("object: {}", value);

        assertNull(value);
    }

    @Test
    public void testFormtString() {
        String string = "foo";

        String value = format.format(string);

        log.debug("string: {}", value);

        assertEquals("foo", value);
    }

    @Test
    public void testFormtStringNewline() {
        String string = "foo\nbar";

        String value = format.format(string);

        log.debug("string: {}", value);

        assertEquals("foo\\nbar", value);
    }

    @Test
    public void testFormtBytes() throws UnsupportedEncodingException {
        byte[] bytes = new String("foo").getBytes();

        String value = format.format(bytes);

        log.debug("bytes: {}", value);

        assertNotNull(value);
    }

    @Test
    public void testFormtPlain() {
        PlainObject plain = new PlainObject();
        plain.setName("plain");

        String value = format.format(plain);

        log.debug("plain: {}", value);

        assertNotNull(value);
    }

    /**
     * Safety check for StackOverflowError.
     */
    @Test
    public void testFormtRecursive() {
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
    private class PlainObject {

        private String name;

    }

    @Data
    private class RecursiveObject {

        private String name;

        private RecursiveObject parent;

        private RecursiveObject child;

    };

}
