
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Checkout {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("cancelUrl")
    @Expose
    private String cancelUrl;
}
