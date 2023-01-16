package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GotoConfirmBookingRequest {
    @SerializedName("activities")
    @Expose
    private List<GotoActivityConfirmationDto> activities = new ArrayList<>();
    @SerializedName("paymentMethod")
    @Expose
    private String paymentMethod = "";
}
