package com.thundashop.core.pmsbookingprocess;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RoomInfo {
    public String roomId = "";
    public List<GuestInfo> guestInfo = new ArrayList();
    public Integer guestCount = 1;
    public Integer maxGuests = 0;
    public Date start;
    public Date end;
    public String roomName = "";
    public HashMap<String, AddonItem> addonsAvailable = new HashMap();
    public Double totalCost = 0.0;
    public String bookingItemTypeId = "";
}
