package com.thundashop.core.bookingengine.data;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class RegistrationRules implements Serializable {
    public LinkedHashMap<String, String> resultAdded = new LinkedHashMap();
    public boolean includeGuestData = false;
    public LinkedHashMap<String, RegistrationRulesField> data = new LinkedHashMap();
}