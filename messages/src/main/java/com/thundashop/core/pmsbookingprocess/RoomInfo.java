package com.thundashop.core.pmsbookingprocess;

import com.thundashop.core.pmsmanager.PmsBookingRooms;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        
    /**
     * Key = Group Addon product Id.
     * Value = List of different options.
     */
    public List<GroupAddonItem> availableGuestOptionAddons = new ArrayList();

    int getNumberOfDays() {
        return PmsBookingRooms.getNumberOfDays(start, end);
    }

}
