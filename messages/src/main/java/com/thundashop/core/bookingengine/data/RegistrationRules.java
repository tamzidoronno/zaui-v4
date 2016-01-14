package com.thundashop.core.bookingengine.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RegistrationRules implements Serializable {
    public boolean includeGuestData = false;
    public LinkedHashMap<String, RegistrationRulesField> userData = new LinkedHashMap();
    public LinkedHashMap<String, RegistrationRulesField> additionalFields = new LinkedHashMap();
}