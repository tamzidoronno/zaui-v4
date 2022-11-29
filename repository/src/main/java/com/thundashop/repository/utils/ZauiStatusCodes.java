package com.thundashop.repository.utils;

public enum ZauiStatusCodes {

    OCTO_FAILED(500,"Octo API call failed");
    public final long code;
    public final String message;

    private ZauiStatusCodes(long code, String message) {
        this.code = code;
        this.message = message;
    }
}
