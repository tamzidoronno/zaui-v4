
package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Orderer {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("mobile")
    @Expose
    private Mobile mobile;

}
