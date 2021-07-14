package com.woozooha.adonistrack.writer;

import java.util.List;

import com.woozooha.adonistrack.domain.Invocation;

public interface History {

    List<Invocation> getInvocationList();
    
}
