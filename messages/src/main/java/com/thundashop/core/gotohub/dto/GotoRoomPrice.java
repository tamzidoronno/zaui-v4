package com.thundashop.core.gotohub.dto;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data

public class GotoRoomPrice {
    @SerializedName("total_room_price")
    @Expose
    private Double totalRoomPrice;
    @SerializedName("daily_prices")
    @Expose
    private List<GotoRoomDailyPrice> dailyPrices;
}
