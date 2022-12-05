package com.thundashop.repository.utils;

public enum ZauiStatusCodes {

    OCTO_FAILED(500,"Octo API call failed"),
    MISSING_PARAMS(500, "Missing parameters");
    public final long code;
    public final String message;

    private ZauiStatusCodes(long code, String message) {
        this.code = code;
        this.message = message;
    }
}
