
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Charge {

    @SerializedName("chargeId")
    @Expose
    private String chargeId;
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("orderItems")
    @Expose
    private List<OrderItem> orderItems = new ArrayList<>();
}
