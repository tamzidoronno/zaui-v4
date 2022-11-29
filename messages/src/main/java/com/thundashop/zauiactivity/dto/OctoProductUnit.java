package com.thundashop.zauiactivity.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OctoProductUnit {
    private String id;
    private String title;
    private String internalName;
    private List<PricingFrom> pricingFrom;
}
class PricingFrom {
    @SerializedName("default")
    @Expose
    private boolean IsDefault;
    private String currency;
    private String  currencyPrecision;
    private String  subtotal;
    private String  taxes;

}
