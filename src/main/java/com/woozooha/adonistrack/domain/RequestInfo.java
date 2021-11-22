package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class RequestInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String type = "REQUEST";

    private String method;

    private String requestURI;

    private String queryString;

    private List<Header> headers;

    private String payload;

    public RequestInfo() {
    }

    public RequestInfo(HttpServletRequest request) {
        this.method = request.getMethod();
        this.requestURI = request.getRequestURI().toString();
        this.queryString = request.getQueryString();

//        this.headers = this.parseHeaders(request);
    }

//    private List<Header> parseHeaders(HttpServletRequest request) {
//        List<Header> headers = new ArrayList<>();
//
//        Enumeration<String> names = request.getHeaderNames();
//        while (names.hasMoreElements()) {
//            String name = names.nextElement();
//
//            Enumeration<String> values = request.getHeaders(name);
//            List<String> valueList = new ArrayList<>();
//            while (values.hasMoreElements()) {
//                valueList.add(values.nextElement());
//            }
//
//            headers.add(new Header(name, valueList));
//        }
//
//        return headers;
//    }

}
