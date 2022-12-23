package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;

@NoArgsConstructor
@Data
public class GotoConfirmBookingRes {
    @SerializedName("paymentLink")
    private String paymentLink;
    public GotoConfirmBookingRes(String paymentLink) {
        this.paymentLink = paymentLink;
    }
}
