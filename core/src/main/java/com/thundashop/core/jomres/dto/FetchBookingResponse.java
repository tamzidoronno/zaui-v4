package com.thundashop.core.jomres.dto;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class FetchBookingResponse implements Serializable {
    @Expose
    long bookingId;
    @Expose
    String pmsBookingId;
    @Expose
    String status;
    @Expose
    String pmsRoomName = "Not Found";
    @Expose
    String pmsRoomCategoryName = "Not Found";
    @Expose
    String guestName;
    @Expose
    String arrivalDate;
    @Expose
    String departureDate;
    public  FetchBookingResponse(){

    }
    public FetchBookingResponse(long bookingId, String status, String guestName, Date arrivalDate, Date departureDate,
                                String pmsBookingId,String pmsRoomName, String pmsRoomCategoryName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.bookingId = bookingId;
        this.status = status;
        this.guestName = guestName;
        this.arrivalDate = dateFormat.format(arrivalDate);
        this.departureDate = dateFormat.format(departureDate);
        this.pmsRoomName = pmsRoomName;
        this.pmsRoomCategoryName = pmsRoomCategoryName;
        this.pmsBookingId = pmsBookingId;
    }
    public void setArrivalDate(Date arrivalDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.arrivalDate = dateFormat.format(arrivalDate);
    }

    public void setDepartureDate(Date departureDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.departureDate = dateFormat.format(departureDate);
    }

}
