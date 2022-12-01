package com.thundashop.core.gotohub.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GotoException extends Exception {
    private long statusCode;
    private String message;

    public GotoException(long statusCode, String message) {
        super();
        this.statusCode = statusCode;
        this.message = message;
    }
}
