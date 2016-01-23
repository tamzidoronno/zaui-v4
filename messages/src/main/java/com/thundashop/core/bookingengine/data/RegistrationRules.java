package com.thundashop.core.bookingengine.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RegistrationRules implements Serializable {
    public LinkedHashMap<String, String> resultAdded = new LinkedHashMap();
    public boolean includeGuestData = false;
    public boolean displayContactsList = false;
    public LinkedHashMap<String, RegistrationRulesField> data = new LinkedHashMap();
    public List<Contacts> contactsList = new ArrayList();
}