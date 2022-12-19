package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RatePlanCode {
    @SerializedName("ratePlanCode")
    @Expose
    private String ratePlanCode;

    public RatePlanCode(String ratePlanCode) {
        this.ratePlanCode = ratePlanCode;
    }
}
