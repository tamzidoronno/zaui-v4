
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Refund {

    @SerializedName("refundId")
    @Expose
    private String refundId;
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("lastUpdated")
    @Expose
    private String lastUpdated;
    @SerializedName("orderItems")
    @Expose
    private List<OrderItem> orderItems = new ArrayList<>();
}
