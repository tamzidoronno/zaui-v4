/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class DatePricedLibrary {
    private List<Date> dates = new ArrayList();
    
    private double extra = 400;

    public DatePricedLibrary() {
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 3, 18);
        dates.add(cal.getTime());
        
        cal.set(2015, 3, 20);
        dates.add(cal.getTime());
    }
    
    public double getPrice(CartItem cartItem, Date date) {
        Calendar cal = Calendar.getInstance();
        
        for (Date checkDate : dates) {
            cal.setTime(checkDate);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date startDate = cal.getTime();
            
            cal.setTime(checkDate);
            cal.set(Calendar.HOUR, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endDate = cal.getTime();
            
            if (date.after(startDate) && date.before(endDate)) {
                return cartItem.getProduct().getPrice(null) + extra;
            }
        }
        
        return cartItem.getProduct().getPrice(null);
    }
}
