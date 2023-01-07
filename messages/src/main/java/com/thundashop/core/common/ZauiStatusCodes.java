package com.thundashop.core.common;

public enum ZauiStatusCodes {

    OCTO_FAILED(500, "Octo API call failed"),
    MISSING_PARAMS(400, "Missing parameters"),
    ACCOUNTING_ERROR(500, "Accounting group incompatible with Zaui Stay"),
    ACTIVITY_NOT_FOUND(404, "Activity/option not found in Zaui Stay"),
    SUPPLIER_NOT_FOUND(404, "Supplier not found in Zaui Stay");

    public final long code;
    public final String message;

    private ZauiStatusCodes(long code, String message) {
        this.code = code;
        this.message = message;
    }
}
