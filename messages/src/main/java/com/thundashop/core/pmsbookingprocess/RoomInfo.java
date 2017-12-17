package com.thundashop.core.pmsbookingprocess;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RoomInfo {
    public String roomId = "";
    public List<GuestInfo> guestInfo = new ArrayList();
    public Integer guestCount = 1;
    public Date start;
    public Date end;
    public String roomName = "";
    public HashMap<String, AddonItem> addonsAdded = new HashMap();
    public HashMap<String, AddonItem> addonsAvailable = new HashMap();
}
