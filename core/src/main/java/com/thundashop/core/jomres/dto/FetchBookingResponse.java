package com.thundashop.core.jomres.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FetchBookingResponse implements Serializable {
    long bookingId;
    String pmsBookingId;
    String status;
    String pmsRoomName;
    String pmsRoomCategoryName;
    String guestName;
    String arrivalDate;
    String departureDate;
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
}
