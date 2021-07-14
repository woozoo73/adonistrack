package com.woozooha.adonistrack.domain;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
public class ResponseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int status;
    
    @Getter(value = AccessLevel.NONE)
    private HttpServletResponse response;

    public ResponseInfo() {
    }

    public ResponseInfo(HttpServletResponse response) {
        this.status = response.getStatus();
        this.response = response;
    }

}
