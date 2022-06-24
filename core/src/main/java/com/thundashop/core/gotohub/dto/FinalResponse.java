package com.thundashop.core.gotohub.dto;

import lombok.Data;

@Data
public class FinalResponse {
    private String status;
    private long statusCode;
    private String messgae;
    private Object response;

    public FinalResponse() {
    }

    public FinalResponse(String status, long statusCode, String messgae, Object response) {
        this.status = status;
        this.statusCode = statusCode;
        this.messgae = messgae;
        this.response = response;
    }
}
