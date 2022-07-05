package com.thundashop.core.gotohub.dto;

import lombok.Data;

@Data
public class FinalResponse {
    private boolean success;
    private long statusCode;
    private String message;
    private Object response;

    public FinalResponse() {
    }

    public FinalResponse(boolean success, long statusCode, String message, Object response) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        this.response = response;
    }
}
