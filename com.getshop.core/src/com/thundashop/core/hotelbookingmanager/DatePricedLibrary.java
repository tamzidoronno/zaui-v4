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
    
    private double extra = 500;

    public DatePricedLibrary() {
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 6, 16);
        dates.add(cal.getTime());
        cal.set(2015, 6, 17);
        dates.add(cal.getTime());
        cal.set(2015, 6, 18);
        dates.add(cal.getTime());
        
        cal.set(2015, 6, 9);
        dates.add(cal.getTime());
        cal.set(2015, 6, 10);
        dates.add(cal.getTime());
        cal.set(2015, 6, 11);
        dates.add(cal.getTime());
        cal.set(2015, 6, 12);
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
                // Expensive family room.
                double add = cartItem.getProduct().id.equals("248ec601-ee7a-4d3a-8644-be4de68b2412") ? 1805 : extra;
                return cartItem.getProduct().getPrice(null) + add;
            }
        }
        
        return cartItem.getProduct().getPrice(null);
    }
}
