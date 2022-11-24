
package com.thundashop.core.gotohub.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GotoBookingResponse {
    @SerializedName("reservationId")
    @Expose
    private String reservationId;
    @SerializedName("priceTotal")
    @Expose
    private PriceTotal priceTotal;
    @SerializedName("hotelCode")
    @Expose
    private String hotelCode;    
    @SerializedName("rooms")
    @Expose
    private List<GotoRoom> rooms = null;
    @SerializedName("ratePlans")
    @Expose
    private List<RatePlanCode> ratePlans = null;
    @SerializedName("roomTypes")
    @Expose
    private List<RoomTypeCode> roomTypes = null;
}
