/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.google.gson.Gson;
import com.thundashop.core.productmanager.data.Product;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
    public String groupedById = "";
    public String addedBy = "";
    public boolean removedAfterDeleted;
    
    public CartItem() {
    }
    
    public Double getPriceExForMinutes() {
        if(startDate == null || endDate == null) {
            return 0.0;
        }
        long diff = endDate.getTime() - startDate.getTime();
        long mins = getSeconds();
        if(mins == 0) {
            return 0.0;
        }
        return (getProduct().priceExTaxes * getCount()) / mins;
    }
    
    public Double getPriceIncForMinutes() {
        if(startDate == null || endDate == null) {
            return 0.0;
        }
        long mins = getSeconds();
        if(mins == 0) {
            return 0.0;
        }
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

    public int getSecondsForDay(Calendar time, boolean shift) {
        if(startDate == null || endDate == null) {
            return -1;
        }
        
        if(startDate.equals(endDate)) {
            return -1;
        }
        
        if(startDate.after(endDate)) {
            return -1;
        }
        
        if(sameDay(startDate, endDate)) {
            return -1;
        }
        
        Date tmpStartDate = new Date(startDate.getTime());
        Date tmpEndDate = new Date(endDate.getTime());
        if(shift) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(tmpStartDate);
            int toShift = cal.get(Calendar.HOUR_OF_DAY);
            cal.add(Calendar.HOUR_OF_DAY, toShift * -1);
            tmpStartDate = cal.getTime();
            
            cal.setTime(tmpEndDate);
            cal.add(Calendar.HOUR_OF_DAY, toShift * -1);
            tmpEndDate = cal.getTime();
        }
        
        Date dayToCheckAgainst = time.getTime();
        int seconds = 0;
        if(sameDay(dayToCheckAgainst, tmpStartDate) && sameDay(dayToCheckAgainst, tmpEndDate)) {
            LocalDateTime start = LocalDateTime.ofInstant(tmpStartDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(tmpEndDate.toInstant(), ZoneId.systemDefault());
            seconds = (int) Duration.between(start, end).getSeconds();
        } else if(sameDay(dayToCheckAgainst, tmpStartDate)) {
            Calendar c = Calendar.getInstance();
            c.setTime(tmpStartDate);
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            
            LocalDateTime start = LocalDateTime.ofInstant(tmpStartDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(c.getTime().toInstant(), ZoneId.systemDefault());

            seconds = (int) Duration.between(start, end).getSeconds();
        } else if(sameDay(dayToCheckAgainst, tmpEndDate)) {
            Calendar c = Calendar.getInstance();
            c.setTime(tmpEndDate);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            
            LocalDateTime start = LocalDateTime.ofInstant(c.getTime().toInstant(), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(tmpEndDate.toInstant(), ZoneId.systemDefault());

            seconds = (int) Duration.between(start, end).getSeconds();
        } else if(dayToCheckAgainst.before(tmpStartDate)) {
            seconds = 0;
        } else if(dayToCheckAgainst.after(tmpEndDate)) {
            seconds = 0;
        } else {
            seconds = (60*60*24);
        }
        
        return seconds;
    }
    
    public boolean sameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate1.equals(localDate2);
    }

    public long getSeconds() {
        
        LocalDateTime start = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
        
        long minutes = Duration.between(start, end).getSeconds();

        return minutes;
    }

    public void refreshCartItemId() {
        cartItemId = UUID.randomUUID().toString();
    }
}
