package com.thundashop.core.jomres.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FetchBookingResponse {
    long bookingId;
    String status;
    String pmsBookingItemId;
    String pmsBookingItemTypeId;
    String guestName;
    String arrivalDate;
    String departureDaTe;
    public FetchBookingResponse(long bookingId, String status, String guestName, Date arrivalDate, Date departureDate,
                                String pmsBookingItemId, String pmsBookingItemTypeId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.bookingId = bookingId;
        this.status = status;
        this.guestName = guestName;
        this.arrivalDate = dateFormat.format(arrivalDate);
        this.departureDaTe = dateFormat.format(departureDate);
        this.pmsBookingItemId = pmsBookingItemId;
        this.pmsBookingItemTypeId = pmsBookingItemTypeId;
    }
}
