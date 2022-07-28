
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OrderDetails {

    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("reference")
    @Expose
    private String reference;
}
