
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Consumer {
    @SerializedName("shippingAddress")
    @Expose
    private ShippingAddress shippingAddress;
    @SerializedName("company")
    @Expose
    private Company company;
    @SerializedName("privatePerson")
    @Expose
    private PrivatePerson privatePerson;
    @SerializedName("billingAddress")
    @Expose
    private BillingAddress billingAddress;
}
