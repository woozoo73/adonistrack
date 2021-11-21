package com.woozooha.adonistrack.domain;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

@Getter
@Setter
public class ResponseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int status;

    private String reasonPhrase;

    private String payload;

    public ResponseInfo() {
    }

    public ResponseInfo(HttpServletResponse response) {
        this.status = response.getStatus();
    }

}
