/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class LoginHistory extends DataCommon {
    private Map<String, List<Date>> logins = new HashMap();
    
    public void markLogin(User user) {
        List<Date> dates = logins.get(user.id);
        if (dates == null) {
            dates = new ArrayList();
        }
        
        dates.add(new Date());
        logins.put(user.id, dates);
    }

    public int getLogins(int year, int month) {
        int i = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        
        for (List<Date> dates : logins.values()) {
            for (Date date : dates) {
                if (date.before(endDate) && date.after(startDate)) {
                    i++;
                }
            }
        }
        
        return i;
    }
}
