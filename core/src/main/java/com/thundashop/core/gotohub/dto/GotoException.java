package com.thundashop.core.gotohub.dto;

import lombok.Data;

@Data
public class GotoException extends Exception{
    private long statusCode;
    private String message;

    public GotoException(long statusCode, String message) {
        super();
        this.statusCode = statusCode;
        this.message = message;
    }
}
