package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thundashop.zauiactivity.dto.OctoBooking;
import lombok.Data;

@Data
public class GotoActivityConfirmationDto {
    @SerializedName("octoConfirmationResponse")
    @Expose
    private OctoBooking octoConfirmationResponse;
    @SerializedName("octoReservationId")
    @Expose
    private String octoReservationId;
}
