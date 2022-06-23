
package com.thundashop.core.gotohub.dto;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class BookingResponse {

    @SerializedName("reservationId")
    @Expose
    private String reservationId;
    @SerializedName("priceTotal")
    @Expose
    private PriceTotal priceTotal;
    @SerializedName("hotelCode")
    @Expose
    private String hotelCode;
    @SerializedName("checkInDate")
    @Expose
    private String checkInDate;
    @SerializedName("checkOutDate")
    @Expose
    private String checkOutDate;
    @SerializedName("rooms")
    @Expose
    private List<Room> rooms = null;
    @SerializedName("ratePlans")
    @Expose
    private List<RatePlan> ratePlans = null;
    @SerializedName("roomTypes")
    @Expose
    private List<RoomType> roomTypes = null;

}
