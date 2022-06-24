package com.thundashop.core.gotohub.dto;

import lombok.Data;

@Data
public class FinalResponse {
    private String status;
    private long statusCode;
    private String messgae;
    private Object response;
}
