/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.google.gson.Gson;
import com.thundashop.core.productmanager.data.Product;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CartItem implements Serializable {
    private String cartItemId = UUID.randomUUID().toString();
    private Map<String, String> variations = new HashMap();
    
    private Product product;
    
    private int count = 0;
    public Date startDate;
    public Date endDate;
    public Date newStartDate;
    public Date newEndDate;
    public Date periodeStart = null;

    public CartItem() {
    }
    
    public Double getPriceExForMinutes() {
        if(startDate == null || endDate == null) {
            return 0.0;
        }
        long diff = endDate.getTime() - startDate.getTime();
        long mins = diff / 60000;
        return (getProduct().priceExTaxes * getCount()) / mins;
    }
    
    public Double getPriceIncForMinutes() {
        if(startDate == null || endDate == null) {
            return 0.0;
        }
        long diff = endDate.getTime() - startDate.getTime();
        long mins = diff / 60000;
        return (getProduct().price * getCount()) / mins;
    }
    
    public boolean isSame(String productId, Map<String, String> variations) {
        if (!this.product.id.equals(productId)) {
            return false;
        }
        if(this.variations == null) {
            return false;
        }
        if (variations.size() != this.variations.size()) {
            return false;
        }
        
        for (String varId : this.variations.values()) {
            if (!variations.values().contains(varId)) {
                return false;
            }
        }
        
        return true;
    }
    
    public String getCartItemId() {
        return cartItemId;
    }

    public void increseCounter() {
        this.count++;
    }
    
    public void decreaseCounter() {
        if (this.count > 1) {
            this.count++;
        }
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setVariations(Map<String, String> variations) {
        this.variations = variations;
    }

    public Product getProduct() {
        return product;
    }

    public Map<String, String> getVariations() {
        if(variations == null) {
            return new HashMap();
        }
        return variations;
    }

    public void doFinalize() {
        product.doFinalize();
        //Negative values on the price should never happen, the count should be negative instead.
        if(product.price < 0) {
            count *= -1;
            product.price *= -1;
            product.priceExTaxes *= -1;
        }
    }

    public Date getStartingDate() {
        if(newStartDate != null) {
            return newStartDate;
        }
        return startDate;
    }
    
    public Date getEndingDate() {
        if(newEndDate != null) {
            return newEndDate;
        }
        return endDate;
    }

    public CartItem copy() {
        Gson gson = new Gson();
        String res = gson.toJson(this);
        CartItem newItem = gson.fromJson(res, CartItem.class);
        newItem.cartItemId = UUID.randomUUID().toString();
        return newItem;
    }

    public boolean startsOnDate(Date time, Date rowCreatedDate) {
        Date startToUse = startDate;
        if(startToUse == null) {
            startToUse = rowCreatedDate;
        }
        if(startToUse == null) {
            return false;
        }
        Calendar createdCal = Calendar.getInstance();
        createdCal.setTime(startToUse);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        
        if((createdCal.get(Calendar.YEAR) == timeCal.get(Calendar.YEAR)) && 
            (createdCal.get(Calendar.DAY_OF_YEAR) == timeCal.get(Calendar.DAY_OF_YEAR))) {
            return true;
        }
        return false;
    }

    public double getNumberOfMinutesForDay(Calendar time) {
        if(startDate == null) {
            return -1;
        }
        
        int counter = 0;
        Calendar startOfDay = (Calendar) time.clone();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        
        if(startOfDay.getTime().after(endDate)) {
            return 0.0;
        }
        
        Calendar endOfDay = (Calendar) time.clone();
        endOfDay.add(Calendar.DAY_OF_YEAR, 1);
        
        if(endOfDay.getTime().before(startDate)) {
            return 0.0;
        }
        if(endOfDay.getTime().before(startDate)) {
//            return 0.0;
        }
        
        for(int i = 0; i < 1440; i++) {
            Date currentToCheck = startOfDay.getTime();
            if(currentToCheck.after(startDate) && currentToCheck.before(endDate)) {
                counter++;
            }
            if(currentToCheck.after(endDate)) {
                break;
            }
            startOfDay.add(Calendar.MINUTE, 1);
        }
        
        return counter;
    }
}
