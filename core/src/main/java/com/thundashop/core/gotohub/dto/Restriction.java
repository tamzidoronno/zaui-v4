package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class Restriction implements Serializable {
    @SerializedName("min_stay")
    @Expose
    private Integer minStay;
    @SerializedName("no_checkin")
    @Expose
    private Integer noCheckin;
    @SerializedName("no_checkout")
    @Expose
    private Integer noCheckout;
}
