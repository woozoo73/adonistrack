package com.woozooha.adonistrack.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.format.Format;

import lombok.Getter;

public class MemoryWriter implements Writer, History {

    @Getter
    private List<Invocation> invocationList = new ArrayList<>();

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

        invocationList.add(invocation);

        if (maxSize > 0 && invocationList.size() > maxSize) {
            int toIndex = invocationList.size();
            int fromIndex = toIndex - maxSize;

            invocationList = invocationList.subList(fromIndex, toIndex);
        }
    }

}
