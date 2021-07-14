package com.woozooha.adonistrack.writer;

import java.util.ArrayList;
import java.util.List;

import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.format.Format;

import lombok.Getter;

public class MemoryWriter implements Writer, History {

    @Getter
    private List<Invocation> invocationList = new ArrayList<>();

    private int maxSize = -1;

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

    @Override
    public void write(Invocation invocation) {
        invocationList.add(invocation);

        if (maxSize > 0 && invocationList.size() > maxSize) {
            int toIndex = invocationList.size();
            int fromIndex = toIndex - maxSize;

            invocationList = invocationList.subList(fromIndex, toIndex);
        }
    }

}
