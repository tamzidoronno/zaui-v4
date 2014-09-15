
package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.usermanager.data.User;

public class MSQLActors {
    public String Nm;
    public String Ad1;
    public String PNo;
    public String PArea;
    public String MailAd;
    public String MobPh;
    
    public boolean compareWithUser(User user) {
        try {
            if(Nm.equalsIgnoreCase(user.fullName) && MailAd.equalsIgnoreCase(user.emailAddress) && MobPh.equalsIgnoreCase(user.cellPhone)) {
                return true;
            }
        }catch(NullPointerException e) {
            return false;
        }
        
        return false;
    }
}
