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
package com.woozooha.adonistrack.callback;

import com.woozooha.adonistrack.Invocation;
import com.woozooha.adonistrack.writer.Writer;

/**
 * Invocation callback write the invocation.
 * 
 * @author woozoo73
 */
public class WriterCallback implements InvocationCallback {

    private Writer writer;

    @Override
    public void before(Invocation invocation) {
    }

    @Override
    public void after(Invocation invocation) {
        writer.write(invocation);
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

}
