package com.thundashop.core.gotohub.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GotoRoomRestriction implements Serializable {
    @SerializedName("min_stay")
    @Expose
    private Integer minStay;
    @SerializedName("max_stay")
    @Expose
    private Integer maxStay;
    @SerializedName("no_checkin")
    @Expose
    private Boolean noCheckin;
    @SerializedName("no_checkout")
    @Expose
    private Boolean noCheckout;
}
