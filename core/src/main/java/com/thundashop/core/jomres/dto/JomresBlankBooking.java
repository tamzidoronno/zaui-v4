package com.thundashop.core.jomres.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.PermenantlyDeleteData;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@PermenantlyDeleteData
@Data
public class JomresBlankBooking extends DataCommon {
    private long contractId=-1;
    private int propertyId;
    private String flatBookingId;
    @SerializedName("date_from")
    @Expose
    private String dateFrom;
    @SerializedName("date_to")
    @Expose
    private String dateTo;


    public JomresBlankBooking() {
    }

    public JomresBlankBooking(long contractId, String flatBookingId, int propertyId, String dateFrom, String dateTo) {
        this.propertyId = propertyId;
        this.flatBookingId = flatBookingId;
        this.contractId = contractId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }
    public JomresBlankBooking(long contractId, String flatBookingId, int propertyId, Date dateFrom, Date dateTo) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        this.propertyId = propertyId;
        this.flatBookingId = flatBookingId;
        this.contractId = contractId;
        this.dateFrom = formatter.format(dateFrom);
        this.dateTo = formatter.format(dateTo);
    }

}
