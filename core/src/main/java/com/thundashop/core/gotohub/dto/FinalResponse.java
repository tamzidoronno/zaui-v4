package com.thundashop.core.gotohub.dto;

import lombok.Data;

@Data
public class FinalResponse {
    private String status;
    private long statusCode;
    private String message;
    private Object response;

    public FinalResponse() {
    }

    public FinalResponse(String status, long statusCode, String message, Object response) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.response = response;
    }
}
