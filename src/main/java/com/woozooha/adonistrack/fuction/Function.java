package com.woozooha.adonistrack.fuction;

public interface Function<T, R> {

    R apply(T t);

}
