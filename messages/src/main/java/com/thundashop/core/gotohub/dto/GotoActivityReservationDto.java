package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thundashop.zauiactivity.dto.OctoBooking;
import lombok.Data;

@Data
public class GotoActivityReservationDto {
    @SerializedName("octoReservationResponse")
    @Expose
    private OctoBooking octoReservationResponse;
    @SerializedName("supplierId")
    @Expose
    private Integer supplierId = null;
}
