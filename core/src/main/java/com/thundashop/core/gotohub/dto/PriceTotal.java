
package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PriceTotal {

    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("amount")
    @Expose
    private Double amount;

}
