package com.thundashop.core.bookingengine.data;

import java.io.Serializable;
import java.util.HashMap;

public class RegistrationRules implements Serializable {
    public boolean includeGuestData = false;
    public HashMap<String, RegistrationRulesField> userData = new HashMap();
    public HashMap<String, RegistrationRulesField> additionalFields = new HashMap();
}