package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GotoConfirmBookingRequest {
    @SerializedName("activities")
    @Expose
    private List<GotoActivityConfirmationDto> activities = null;
    @SerializedName("paymentMethod")
    @Expose
    private String paymentMethod = null;
}
