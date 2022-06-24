package com.thundashop.core.availability.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class Contact implements Serializable {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("organization_number")
    @Expose
    private String organizationNumber;
}
