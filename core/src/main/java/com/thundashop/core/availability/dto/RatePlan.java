package com.thundashop.core.availability.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
public class RatePlan implements Serializable {

    @SerializedName("rate_plan_code")
    @Expose
    private String ratePlanCode;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("about")
    @Expose
    private String about;

    @SerializedName("meal_included")
    @Expose
    private boolean mealIncluded;

    @SerializedName("restriction")
    @Expose
    private String restriction;

    @SerializedName("effective_date")
    @Expose
    private String effectiveDate;

    @SerializedName("expire_date")
    @Expose
    private String expireDate;

    @SerializedName("cancelation_policy")
    @Expose
    private String cancelationPolicy;

    @SerializedName("guest_count")
    @Expose
    private String guestCount;

    @SerializedName("cancelation_poloicy")
    @Expose
    private String cancelationPoloicy;
}
