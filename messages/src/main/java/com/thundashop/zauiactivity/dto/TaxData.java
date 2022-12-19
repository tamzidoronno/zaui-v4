package com.thundashop.zauiactivity.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class TaxData {
    @SerializedName("taxType")

    private String taxType;
    @SerializedName("name")

    private String name;
    @SerializedName("accountingCode")

    private long accountingCode;
    @SerializedName("rate")

    private Integer rate;
    @SerializedName("priceExcludingTax")

    private double priceExcludingTax;
    @SerializedName("taxAmount")

    private double taxAmount;
}
