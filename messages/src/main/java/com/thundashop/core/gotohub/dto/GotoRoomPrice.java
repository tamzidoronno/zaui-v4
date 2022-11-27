package com.thundashop.core.gotohub.dto;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data

public class GotoRoomPrice {
    @SerializedName("totalRoomPrice")
    @Expose
    private Double totalRoomPrice;
    @SerializedName("dailyPrices")
    @Expose
    private List<GotoRoomDailyPrice> dailyPrices;
}
