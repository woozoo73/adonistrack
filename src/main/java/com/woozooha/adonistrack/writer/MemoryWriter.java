package com.woozooha.adonistrack.writer;

import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.format.Format;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class MemoryWriter implements Writer, History {

    @Getter
    private LinkedList<Invocation> invocations = new LinkedList<>();

    private int maxSize = -1;

    private Predicate<Invocation> filter = (t) -> true;

    @Override
    public Format getFormat() {
        return null;
    }

    @Override
    public void setFormat(Format format) {
        // do nothing.
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Predicate<Invocation> getFilter() {
        return filter;
    }

    public void setFilter(Predicate<Invocation> filter) {
        this.filter = filter;
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
