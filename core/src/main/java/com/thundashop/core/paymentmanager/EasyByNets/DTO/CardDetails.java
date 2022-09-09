
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CardDetails {

    @SerializedName("maskedPan")
    @Expose
    private String maskedPan;
    @SerializedName("expiryDate")
    @Expose
    private String expiryDate;
}
