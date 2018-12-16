package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PmsGuests implements Serializable {
    public String name;
    public String phone;
    public String email;
    public String prefix = "47";
    public String guestId = UUID.randomUUID().toString();
    public boolean isChild = false;
    
    /**
     * If the guests has the possibility
     * to order different options of a group addon type (option) 
     * it will be filled in here what option the guest has chosen.
     * 
     * Key = productid for group addon
     * Value = ProductId for the addon selected
     */
    public Map<String, String> orderedOption = new HashMap();
}
