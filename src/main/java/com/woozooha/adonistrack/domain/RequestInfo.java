package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@Getter
@Setter
public class RequestInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected String type = "REQUEST";

    private String method;

    private String requestURI;

    private String queryString;

    private String payload;

    public RequestInfo() {
    }

    public RequestInfo(HttpServletRequest request) {
        this.method = request.getMethod();
        this.requestURI = request.getRequestURI().toString();
        this.queryString = request.getQueryString();
    }

}
