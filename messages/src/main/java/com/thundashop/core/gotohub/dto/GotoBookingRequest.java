
package com.thundashop.core.gotohub.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GotoBookingRequest {
    @SerializedName("reservationId")
    @Expose
    private String reservationId;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("currency")
    @Expose
    private String currency;    
    @SerializedName("hotelCode")
    @Expose
    private String hotelCode;
    @SerializedName("orderer")
    @Expose
    private GotoBooker orderer;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("total_price")
    @Expose
    private Double totalPrice;
    @SerializedName("rooms")
    @Expose
    private List<GotoRoom> rooms = null;
}
