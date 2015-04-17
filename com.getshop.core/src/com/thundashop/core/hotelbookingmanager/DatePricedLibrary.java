/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.cartmanager.data.CartItem;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class DatePricedLibrary {
    private final Date startDate;
    private final Date endDate;
    private double extra = 400;

    public DatePricedLibrary() {
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 3, 19, 0, 0, 0);
        this.startDate = cal.getTime();
        
        cal.set(2015, 3, 20, 23, 59, 59);
        this.endDate = cal.getTime();
    }
    
    public double getPrice(CartItem cartItem, Date date) {
        if (date.after(startDate) && date.before(endDate)) {
            return cartItem.getProduct().getPrice(null) + extra;
        }
        
        return cartItem.getProduct().getPrice(null);
    }
}
