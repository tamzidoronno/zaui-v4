package com.thundashop.core.pmsbookingprocess;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GuestAddonsSummary {
    public List<AddonItem> items = new ArrayList();
    public List<RoomInfo> rooms = new ArrayList();
    public List<String> textualSummary = new ArrayList();
    public LinkedHashMap<String, String> fields = new LinkedHashMap();
    public LinkedHashMap<String, String> fieldsValidation = new LinkedHashMap();
    public String profileType = "";
    public boolean isValid = false;
    public boolean agreedToTerms = false;
}
