package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class LongTermDeal {
    @SerializedName("number_of_days")
    @Expose
    private Integer numbOfDays;

    public LongTermDeal(Integer numbOfDays, Integer discountPercentage) {
        this.numbOfDays = numbOfDays;
        this.discountPercentage = discountPercentage;
    }

    @SerializedName("discount_percentage")
    @Expose
    private Integer discountPercentage;
}
