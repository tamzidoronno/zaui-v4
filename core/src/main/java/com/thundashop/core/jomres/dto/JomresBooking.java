package com.thundashop.core.jomres.dto;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JomresBooking implements Serializable {
    public Date arrivalDate= new Date();
    public Date departure= new Date();
    public long bookingId =0;
    public Date bookingCreated = new Date();
    public String comment= "";
    public long invoiceId=0;
    public long invoiceNumber=0;
    public Date lastModified=new Date();
    public String reservationCode ="";
    public int statusCode=0;
    public String status="";

    public double totalPrice = 0.0;
    public String currencyCode="";
    public double depositAmount =0.0;
    public boolean depositPaid=true;
    public int propertyUid = 0;
    public List<JomresGuest> guests = new ArrayList<JomresGuest>();
    public JomresGuest customer = new JomresGuest();

    public List<JomresBookedRoom> rooms = new ArrayList<JomresBookedRoom>();
    public int numberOfGuests=1;

    public JomresBooking() {

    }

    public JomresBooking(LinkedTreeMap<String, ?> booking) throws ParseException {
        if(booking == null) return;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

        this.currencyCode = booking.get("currency_code").toString();

        LinkedTreeMap guest = (LinkedTreeMap) booking.get("guest_data");
        this.customer = new JomresGuest(guest);

        LinkedTreeMap guestNumber = (LinkedTreeMap) booking.get("guest_numbers");
        this.numberOfGuests = ((Double)guestNumber.get("number_of_guests")).intValue();

        LinkedTreeMap statusMap = (LinkedTreeMap) booking.get("status");
        this.statusCode = ((Double)statusMap.get("status_code")).intValue();
        this.status = statusMap.get("status_text").toString();

        this.comment =booking.get("comments").toString();

        this.arrivalDate = dateFormat.parse(booking.get("date_from").toString());
        this.departure = dateFormat.parse(booking.get("date_to").toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(departure);
        calendar.add(Calendar.DATE, 1);
        this.departure = calendar.getTime();

        this.lastModified = timestampFormat.parse(booking.get("last_modified").toString());
        this.bookingCreated = timestampFormat.parse(booking.get("booking_created").toString());

        this.bookingId = (long) Double.parseDouble(booking.get("booking_id").toString());
        this.reservationCode = booking.get("booking_number").toString();
        this.invoiceNumber = (long) Double.parseDouble(booking.get("invoice_number").toString());
        this.totalPrice = (long) Double.parseDouble(booking.get("booking_total").toString());
        this.depositPaid = (Boolean) booking.get("deposit_paid");
        this.depositAmount =(long) Double.parseDouble(booking.get("deposit_amount").toString());
        this.propertyUid = (int) Double.parseDouble(booking.get("property_uid").toString());
        this.invoiceId =(long) Double.parseDouble(booking.get("invoice_id").toString());
    }
}