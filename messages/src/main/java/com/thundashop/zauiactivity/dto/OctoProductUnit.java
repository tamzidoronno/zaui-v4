package com.thundashop.zauiactivity.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class OctoProductUnit {
    private String id;
    private String title;
    private String internalName;
    private List<PricingFrom> pricingFrom;
}

@Data
class PricingFrom {
    @SerializedName("default")
    @Expose
    private boolean IsDefault;
    private String currency;
    private String  currencyPrecision;
    private String  subtotal;
    private String  taxes;

}
