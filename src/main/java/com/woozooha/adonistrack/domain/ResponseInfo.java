package com.woozooha.adonistrack.domain;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

import lombok.Data;

@Data
public class ResponseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int status;

    public ResponseInfo() {
    }

    public ResponseInfo(HttpServletResponse response) {
        this.status = response.getStatus();
    }

}
