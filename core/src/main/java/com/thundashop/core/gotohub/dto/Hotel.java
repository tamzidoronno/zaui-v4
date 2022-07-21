package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Hotel {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("hotel_code")
    @Expose
    private String hotelCode;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("long")
    @Expose
    private double _long;
    @SerializedName("currency_type")
    @Expose
    private String currencyType;
    @SerializedName("checkin_time")
    @Expose
    private String checkinTime;
    @SerializedName("checkout_time")
    @Expose
    private String checkoutTime;
    @SerializedName("contact_details")
    @Expose
    private Contact contactDetails;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("images")
    @Expose
    private List<String> images = new ArrayList<String>();
}
