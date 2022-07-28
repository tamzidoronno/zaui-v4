
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Company {

    @SerializedName("merchantReference")
    @Expose
    private String merchantReference;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("registrationNumber")
    @Expose
    private String registrationNumber;
    @SerializedName("contactDetails")
    @Expose
    private ContactDetails contactDetails;
}
