package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoomType implements Serializable {
    @SerializedName("hotel_code")
    @Expose
    private String hotelCode;
    @SerializedName("room_type_code")
    @Expose
    private String roomTypeCode;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("number_of_unit")
    @Expose
    private int numberOfUnit;
    @SerializedName("room_category")
    @Expose
    private String roomCategory;
    @SerializedName("size_measurement")
    @Expose
    private String sizeMeasurement;
    @SerializedName("non_smoking")
    @Expose
    private boolean nonSmoking;
    @SerializedName("max_guest")
    @Expose
    private int maxGuest;
    @SerializedName("number_of_adults")
    @Expose
    private int numberOfAdults;
    @SerializedName("number_of_children")
    @Expose
    private int numberOfChildren;
    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("images")
    @Expose
    private List<Object> images = new ArrayList<Object>();
    @SerializedName("rate_plans")
    @Expose
    private List<RatePlan> ratePlans = new ArrayList<RatePlan>();
}
