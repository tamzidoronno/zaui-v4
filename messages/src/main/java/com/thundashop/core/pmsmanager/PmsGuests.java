package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PmsGuests implements Serializable {
    public String name;
    public String phone;
    public String email;
    public String prefix = "47";
    public String guestId = UUID.randomUUID().toString();
    public boolean isChild = false;
    public List<String> pmsConferenceEventIds = new ArrayList();
    
    
    /**
     * If the guests has the possibility
     * to order different options of a group addon type (option) 
     * it will be filled in here what option the guest has chosen.
     * 
     * Key = productid for group addon
     * Value = ProductId for the addon selected
     */
    public Map<String, String> orderedOption = new HashMap();

    boolean hasAnyOfGuest(PmsGuests guest) {
        boolean matchName = true;
        boolean matchEmail = true;
        boolean matchPhone = true;
        
        if(guest.name == null) { guest.name = ""; }
        if(guest.email == null) { guest.email = ""; }
        if(guest.phone == null) { guest.phone = ""; }
        
        if(!guest.name.isEmpty()) {
            if(name == null || !name.toLowerCase().contains(guest.name.toLowerCase())) {
                matchName = false;
            }
        }
        
        if(!guest.email.isEmpty()) {
            if(email == null || !email.toLowerCase().contains(guest.email.toLowerCase())) {
                matchEmail = false;
            }
        }
        if(!guest.phone.isEmpty()) {
            if(phone == null || !phone.toLowerCase().contains(guest.phone.toLowerCase())) {
                matchPhone = false;
            }
        }
        if(guest.phone.isEmpty() && guest.email.isEmpty() && guest.name.isEmpty()) {
            return false;
        }
        return (matchEmail && matchName && matchPhone);
    }

    boolean hasAnyOfGuests(List<PmsGuestOption> guests) {
        for(PmsGuestOption opt : guests) {
            if(hasAnyOfGuest(opt.guest)) {
               return true; 
            }
        }
        return false;
    }
}
