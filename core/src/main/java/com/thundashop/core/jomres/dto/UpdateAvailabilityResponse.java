package com.thundashop.core.jomres.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class UpdateAvailabilityResponse {
    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("contract_id")
    @Expose
    private long contractId = 0;
    @SerializedName("message")
    @Expose
    private String message ="";
    private String start;
    private String end;
    private int propertyId;
    private boolean isAvailable;

    public void setStart(Date start) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        this.start = formatter.format(start);
    }

    public void setEnd(Date end) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        this.end = formatter.format(end);
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public UpdateAvailabilityResponse() {
    }
}
