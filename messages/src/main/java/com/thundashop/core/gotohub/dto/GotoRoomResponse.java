package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class GotoRoomResponse {
    @SerializedName("checkInDate")
    @Expose
    private String checkInDate;
    @SerializedName("checkOutDate")
    @Expose
    private String checkOutDate;

    @SerializedName("adults")
    @Expose
    private Integer adults;
    @SerializedName("cancelationDeadline")
    @Expose
    private String cancelationDeadline;
    @SerializedName("children")
    @Expose
    private List<Integer> childrenAges = null;
    @SerializedName("price")
    @Expose
    private GotoRoomPrice price = null;
}
