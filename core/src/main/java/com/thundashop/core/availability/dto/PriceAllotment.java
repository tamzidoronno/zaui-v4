package com.thundashop.core.availability.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class PriceAllotment implements Serializable {
    @SerializedName("start_date")
    @Expose
    public String startDate;
    @SerializedName("end_date")
    @Expose
    public String endDate;
    @SerializedName("room_type_code")
    @Expose
    public String roomTypeCode;
    @SerializedName("rate_plan_code")
    @Expose
    public String ratePlanCode;
    @SerializedName("allotment")
    @Expose
    public long allotment;
    @SerializedName("price")
    @Expose
    public double price;
}
