
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ContactDetails {

    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phoneNumber")
    @Expose
    private PhoneNumber phoneNumber;
}
