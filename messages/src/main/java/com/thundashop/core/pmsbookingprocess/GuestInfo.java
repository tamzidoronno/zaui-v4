
package com.thundashop.core.pmsbookingprocess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuestInfo {
    public String name;
    public String email;
    public String prefix;
    public String phone;
    public Boolean isChild = false;
    
    /**
     * The selected options 
     * Key = productid for group addon
     * Value = ProductId for the addon selected
     */
    public Map<String, String> selectedOptions = new HashMap();
    
}
