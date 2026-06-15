package com.woozooha.adonistrack.writer;

import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.format.Format;
import com.woozooha.adonistrack.fuction.Predicate;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

public class MemoryWriter implements Writer, History {

    @Getter
    private LinkedList<Invocation> invocations = new LinkedList<Invocation>();

    @Setter
    private int maxSize = -1;

    @Setter
    @Getter
    private Predicate<Invocation> filter = new Predicate<Invocation>() {
        public boolean test(Invocation invocation) {
            return true;
        }
    };

    public Format getFormat() {
        return null;
    }

    public void setFormat(Format format) {
        // do nothing.
    }

    public void write(Invocation invocation) {
        if (!getFilter().test(invocation)) {
            return;
        }

        invocations.addFirst(invocation);

        while (invocations.size() > maxSize) {
            invocations.removeLast();
        }
    }

    public List<Invocation> getInvocationList() {
        return invocations;
    }

}
