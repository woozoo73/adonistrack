package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class ResponseInfo implements Call, Serializable {

    private static final long serialVersionUID = 1L;

    private int status;

    private String reasonPhrase;

    private List<Header> headers;

    private String payload;

    private Long start;

    public ResponseInfo() {
        this.start = System.currentTimeMillis();
    }

    public ResponseInfo(HttpServletResponse response) {
        this.start = System.currentTimeMillis();
        this.status = response.getStatus();
    }

}
