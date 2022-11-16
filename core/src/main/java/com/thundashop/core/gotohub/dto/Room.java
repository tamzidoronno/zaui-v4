
package com.thundashop.core.gotohub.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Room {
    @SerializedName("checkInDate")
    @Expose
    private String checkInDate;
    @SerializedName("checkOutDate")
    @Expose
    private String checkOutDate;
    @SerializedName("roomCode")
    @Expose
    private String roomCode;
    @SerializedName("ratePlanCode")
    @Expose
    private String ratePlanCode;
    @SerializedName("adults")
    @Expose
    private Integer adults;
    @SerializedName("cancelationDeadline")
    @Expose
    private String cancelationDeadline;
    @SerializedName("children")
    @Expose
    private List<Integer> childrenAges = null;

}
