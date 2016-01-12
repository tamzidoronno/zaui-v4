package com.thundashop.core.bookingengine.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegistrationRules {
    public class PmsRegistrationRulesField {
        public boolean required = false;
        public String type = "";
        public List<String> additional = new ArrayList();
        public String dependsOnCondition = "";
        public String title = "";
    }
    
    public boolean includeGuestData = false;
    public HashMap<String, PmsRegistrationRulesField> userData = new HashMap();
    public HashMap<String, PmsRegistrationRulesField> additionalFields = new HashMap();
}