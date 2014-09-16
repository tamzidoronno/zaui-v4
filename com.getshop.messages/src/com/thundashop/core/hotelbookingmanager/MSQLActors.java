
package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.usermanager.data.User;

public class MSQLActors {
    public String Nm;
    public String Ad1;
    public String PNo;
    public String PArea;
    public String MailAd;
    public String MobPh;
    public int customerId;
    
    public boolean compareWithUser(User user) {
        if(customerId == user.customerId) {
            return true;
        }
        return false;
    }
    
    public boolean hasChanged(User user) {
        
        if(!user.fullName.equalsIgnoreCase(Nm)) {
            return true;
        }
        if(user.address != null) {
            if(user.address.address != null && !user.address.address.equalsIgnoreCase(Ad1)) {
                return true;
            }
            if(user.address.postCode != null && !user.address.postCode.equalsIgnoreCase(PNo)) {
                return true;
            }
            if(user.address.city != null && !user.address.city.equalsIgnoreCase(PArea)) {
                return true;
            }
        }
        return false;
    }
}
