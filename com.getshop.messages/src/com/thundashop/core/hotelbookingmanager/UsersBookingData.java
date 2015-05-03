package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsersBookingData extends DataCommon {
    public boolean sentWelcomeMessages = false;
    public List<BookingReference> references = new ArrayList();
    AdditionalBookingInformation additonalInformation = new AdditionalBookingInformation();
    public boolean payedFor = false;
    public boolean partnerReference = false;
    public List<String> orderIds = new ArrayList();
    public boolean active = true;
    public boolean paymentTypeInvoice = false;
    
    /* Average price each day */
    public double bookingPrice = 0.0;
    public String sessionId = "";
    public Date started;
    boolean completed = false;
    boolean captured = false;
    boolean testReservation = false;
    boolean avoidAutoDelete = false;
}
