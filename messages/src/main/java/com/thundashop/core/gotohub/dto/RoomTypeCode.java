package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class RoomTypeCode {
    @SerializedName("roomTypeCode")
    @Expose
    private String roomTypeCode;

    public RoomTypeCode(String roomTypeCode) {
        this.roomTypeCode = roomTypeCode;
    }
}
