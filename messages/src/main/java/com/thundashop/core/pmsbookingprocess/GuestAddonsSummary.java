package com.thundashop.core.pmsbookingprocess;

import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GuestAddonsSummary {
    public List<AddonItem> items = new ArrayList();
    public List<ActiveCampaigns> campaigns = new ArrayList();
    public List<RoomInfo> rooms = new ArrayList();
    public List<String> textualSummary = new ArrayList();
    public LinkedHashMap<String, String> fields = new LinkedHashMap();
    public LinkedHashMap<String, String> fieldsValidation = new LinkedHashMap();
    public String profileType = "";
    public boolean isValid = false;
    public boolean agreedToTerms = false;
    public boolean isLoggedOn = false;
    public String loggedOnName = "";
    public List<BookingZauiActivityItem> zauiActivityItems = new ArrayList<>();

    List<AddonItem> getAllItems() {
        HashMap<String, AddonItem> items = new HashMap();
        for(RoomInfo info : rooms) {
            for(AddonItem item : info.addonsAvailable.values()) {
                items.put(item.productId, item);
            }
        }
        return new ArrayList(items.values());
    }
}
