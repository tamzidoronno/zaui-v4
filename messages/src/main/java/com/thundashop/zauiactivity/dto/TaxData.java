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

    private Integer accountingCode;
    @SerializedName("rate")

    private Integer rate;
    @SerializedName("priceExcludingTax")

    private Integer priceExcludingTax;
    @SerializedName("taxAmount")

    private Integer taxAmount;
}
