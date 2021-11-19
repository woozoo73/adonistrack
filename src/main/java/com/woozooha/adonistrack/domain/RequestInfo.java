package com.woozooha.adonistrack.domain;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import lombok.Data;

@Data
public class RequestInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected String type = "REQUEST";

    private String method;

    private String requestURI;

    private String queryString;

    public RequestInfo() {
    }

    public RequestInfo(HttpServletRequest request) {
        this.method = request.getMethod();
        this.requestURI = request.getRequestURI().toString();
        this.queryString = request.getQueryString();
    }

}
