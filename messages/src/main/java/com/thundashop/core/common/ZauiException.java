package com.thundashop.core.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ZauiException extends Exception {
    private long statusCode;
    private String message;

    public ZauiException(ZauiStatusCodes zauiStatusCodes) {
        super();
        this.statusCode = zauiStatusCodes.code;
        this.message = zauiStatusCodes.message;
    }
    
    public ZauiException(int statusCode, String message) {
        super();
        this.statusCode = statusCode;
        this.message = message;
    }

}
