package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class RequestInfo implements Call, Serializable {

    private static final long serialVersionUID = 1L;

    protected String type = "REQUEST";

    private String method;

    private String requestURI;

    private String queryString;

    private List<Header> headers;

    private String payload;

    private Long start;

    public RequestInfo() {
        this.start = System.currentTimeMillis();
    }

    public RequestInfo(HttpServletRequest request) {
        this.start = System.currentTimeMillis();
        this.method = request.getMethod();
        this.requestURI = request.getRequestURI().toString();
        this.queryString = request.getQueryString();
    }

}
