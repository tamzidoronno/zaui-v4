package com.thundashop.core.jomres.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UnavailabilityDate {
    @SerializedName("date_from")
    @Expose
    private String dateFrom;
    @SerializedName("date_to")
    @Expose
    private String dateTo;

    public UnavailabilityDate(String dateFrom, String dateTo) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }


}
