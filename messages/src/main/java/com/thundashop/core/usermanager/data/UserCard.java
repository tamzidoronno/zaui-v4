package com.thundashop.core.usermanager.data;

import java.io.Serializable;
import java.util.Calendar;

public class UserCard implements Serializable {
    public String card = "";
    public Integer expireMonth = -1;
    public Integer expireYear = -1;
    public String savedByVendor = "";
    public String mask = "";

    public boolean isExpired() {
        Calendar cal = Calendar.getInstance();
        int monthNow = cal.get(Calendar.MONTH)+1;
        int yearNow = cal.get(Calendar.YEAR) % 100;
                
        if(yearNow > expireYear) {
            return true;
        }
        if((monthNow > expireMonth) && (yearNow >= expireYear)) {
            return true;
        }
        
        return false;
    }
}
