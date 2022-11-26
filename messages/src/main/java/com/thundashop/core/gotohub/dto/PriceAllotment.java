package com.thundashop.core.gotohub.dto;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

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
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("rate_plan_code")
    @Expose
    public String ratePlanCode;
    @SerializedName("allotment")
    @Expose
    public long allotment;
    @SerializedName("price")
    @Expose
    public double price;
    @SerializedName("restrictions")
    @Expose
    private GotoRoomRestriction restrictions;
    @SerializedName("long_term_deals")
    private List<LongTermDeal> longTermDeals;
}
