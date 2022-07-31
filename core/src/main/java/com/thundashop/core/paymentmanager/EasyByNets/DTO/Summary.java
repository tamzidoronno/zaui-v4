
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Summary {

    @SerializedName("reservedAmount")
    @Expose
    private double reservedAmount;
    @SerializedName("chargedAmount")
    @Expose
    private double chargedAmount;
    @SerializedName("refundedAmount")
    @Expose
    private double refundedAmount;
    @SerializedName("cancelledAmount")
    @Expose
    private double cancelledAmount;
}
