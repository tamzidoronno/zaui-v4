package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GotoRoomDailyPrice {
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("price")
    @Expose
    private Double price;
}
