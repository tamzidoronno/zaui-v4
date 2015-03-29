package com.thundashop.core.hotelbookingmanager;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Room extends DataCommon {
    public String productId;
    public String roomName;
    public Boolean isActive = true;
    public Boolean isClean = false;
    public BookingReference lastReservation = null;
    public Boolean isHandicap = false;
    public Boolean suitedForLongTerm = false;
    public List<Date> cleaningDates = new ArrayList();
    
}