package com.woozooha.adonistrack.writer;

import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.format.Format;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class MemoryWriter implements Writer, History {

    @Getter
    private LinkedList<Invocation> invocations = new LinkedList<>();

    @Setter
    private int maxSize = -1;

    @Setter
    @Getter
    private Predicate<Invocation> filter = (t) -> true;

    @Override
    public Format getFormat() {
        return null;
    }

    @Override
    public void setFormat(Format format) {
        // do nothing.
    }

    @Override
    public void write(Invocation invocation) {
        if (!getFilter().test(invocation)) {
            return;
        }

        invocations.addFirst(invocation);

        while (invocations.size() > maxSize) {
            invocations.removeLast();
        }
    }

    @Override
    public List<Invocation> getInvocationList() {
        return invocations;
    }

}
