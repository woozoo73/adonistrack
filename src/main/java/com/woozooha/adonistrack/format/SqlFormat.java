package com.woozooha.adonistrack.format;

public interface SqlFormat {

    String format(String sql);

    int getMaxLength();

}
