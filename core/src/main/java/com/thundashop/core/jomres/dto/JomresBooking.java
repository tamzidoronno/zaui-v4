package com.thundashop.core.jomres.dto;

import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.thundashop.core.jomres.services.Constants.ARRIVAL_DEPARTURE_IN_LIST_DATE_FORMAT;
import static com.thundashop.core.jomres.services.Constants.CREATED_MODIFIED_IN_DETAILS_DATE_FORMAT;

public class JomresBooking implements Serializable {
    public Date arrivalDate= new Date();
    public Date departure= new Date();
    public long bookingId =0;
    public Date bookingCreated = new Date();
    public String comment= "";
    public long invoiceId=0;
    public Date lastModified=new Date();
    public String reservationCode ="";
    public int statusCode=0;
    public String status="";

    public double totalPrice = 0.0;
    public String currencyCode="";
    public double depositAmount =0.0;
    public boolean depositPaid=true;
    public int propertyUid = 0;
    public JomresGuest customer = new JomresGuest();

    public int numberOfGuests=1;

    public JomresBooking() {

    }

    public JomresBooking(LinkedTreeMap<String, ?> booking) throws ParseException {
        if(booking == null) return;
        SimpleDateFormat dateFormat = new SimpleDateFormat(ARRIVAL_DEPARTURE_IN_LIST_DATE_FORMAT);
        SimpleDateFormat timestampFormat = new SimpleDateFormat(CREATED_MODIFIED_IN_DETAILS_DATE_FORMAT);

        this.currencyCode = Optional.ofNullable(booking.get("currency_code").toString()).orElse("");

        String firstName =Optional.ofNullable(booking.get("firstname").toString()).orElse("");
        String surName =Optional.ofNullable(booking.get("surname").toString()).orElse("");
        this.customer.name = firstName+ " " + surName;

        String rawLandLine = Optional.ofNullable(booking.get("tel_landline").toString()).orElse("");
        String landLine = rawLandLine.replaceAll("[^0-9]", "");

        String rawMobile = Optional.ofNullable(booking.get("tel_mobile").toString()).orElse("");
        String mobile = rawMobile.replaceAll("[^0-9]", "");

        this.customer.telLandline = landLine;
        this.customer.telMobile = mobile;
        this.customer.email = Optional.ofNullable(booking.get("email").toString()).orElse("");

        this.status = Optional.ofNullable(booking.get("TxtStatus").toString()).orElse("Approved");
        this.statusCode = ((Double)booking.get("cancelled")).intValue() == 1? 6 : 3;

        this.comment =Optional.ofNullable(booking.get("special_reqs").toString()).orElse("");

        this.arrivalDate = dateFormat.parse(booking.get("arrival").toString());
        this.departure = dateFormat.parse(booking.get("departure").toString());
        this.bookingCreated = timestampFormat.parse(booking.get("timestamp").toString());
        this.lastModified = Optional.ofNullable(timestampFormat.parse(booking.get("last_changed").toString())).orElse(bookingCreated);

        this.bookingId = (long) Double.parseDouble(booking.get("contract_uid").toString());
        this.reservationCode = Optional.ofNullable(booking.get("tag").toString()).orElse("");

        this.totalPrice = (long) Double.parseDouble(Optional.ofNullable(booking.get("contract_total").toString()).orElse("0.0"));
        this.depositPaid = Optional.ofNullable(booking.get("deposit_paid").toString()).orElse("0").equals("1");
        this.depositAmount =(long) Double.parseDouble(Optional.ofNullable(booking.get("deposit_required").toString()).orElse("0.0"));
        this.propertyUid = (int) Double.parseDouble(booking.get("property_uid").toString());
        this.invoiceId =(long) Double.parseDouble(Optional.ofNullable(booking.get("invoice_uid").toString()).orElse("0"));
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public void setCustomer(JomresGuest customer) {
        this.customer = customer;
    }
}