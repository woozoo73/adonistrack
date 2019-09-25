/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.woozooha.adonistrack.writer;

import java.util.List;

import com.woozooha.adonistrack.domain.Invocation;
import com.woozooha.adonistrack.format.Format;

/**
 * Composite writer can holds many writers.
 * 
 * @author woozoo73
 */
public class CompositeWriter implements Writer {

    private List<Writer> writers;

    private Format format;

    private boolean appliedFormat;

    @Override
    public Format getFormat() {
        return format;
    }

    @Override
    public void setFormat(Format format) {
        this.format = format;
    }

    @Override
    public void write(Invocation invocation) {
        if (writers == null) {
            return;
        }

        if (!appliedFormat) {
            applyFormat();
        }

        for (Writer writer : writers) {
            writer.write(invocation);
        }
    }

    protected void applyFormat() {
        if (writers == null) {
            return;
        }

        for (Writer writer : writers) {
            writer.setFormat(format);
        }

        appliedFormat = true;
    }

}
