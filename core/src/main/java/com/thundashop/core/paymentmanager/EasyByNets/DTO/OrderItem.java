
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class OrderItem {

    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("quantity")
    @Expose
    private double quantity;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("unitPrice")
    @Expose
    private double unitPrice;
    @SerializedName("taxRate")
    @Expose
    private double taxRate;
    @SerializedName("taxAmount")
    @Expose
    private double taxAmount;
    @SerializedName("grossTotalAmount")
    @Expose
    private double grossTotalAmount;
    @SerializedName("netTotalAmount")
    @Expose
    private double netTotalAmount;
}
