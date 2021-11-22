package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class ResponseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int status;

    private String reasonPhrase;

    private List<Header> headers;

    private String payload;

    public ResponseInfo() {
    }

    public ResponseInfo(HttpServletResponse response) {
        this.status = response.getStatus();

        // this.headers = this.parseHeaders(response);
    }

//    private List<Header> parseHeaders(HttpServletResponse response) {
//        List<Header> headers = new ArrayList<>();
//
//        Collection<String> names = response.getHeaderNames();
//        for (String name : names) {
//            Collection<String> values = response.getHeaders(name);
//            headers.add(new Header(name, new ArrayList<>(values)));
//        }
//
//        return headers;
//    }

}
