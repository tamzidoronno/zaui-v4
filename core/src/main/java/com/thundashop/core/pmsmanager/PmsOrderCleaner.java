
package com.thundashop.core.pmsmanager;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PmsOrderCleaner {

    private final Order order;

    PmsOrderCleaner(Order order) {
        this.order = order;
    }

    Order cleanOrder() {
        List<String> toRemove = new ArrayList();
        for(CartItem item : order.cart.getItems()) {
            for(CartItem item2 : order.cart.getItems()) {
                if(toRemove.contains(item2.getCartItemId())) {
                    continue;
                }
                if(toRemove.contains(item.getCartItemId())) {
                    continue;
                }
                if(isExactOposite(item, item2)) {
                    toRemove.add(item.getCartItemId());
                    toRemove.add(item2.getCartItemId());
                }
            }
        }
        for(String removeItem : toRemove) {
            order.cart.removeItem(removeItem);
        }
        toRemove.clear();
        
        for(CartItem item1 : order.cart.getItems()) {
           for(CartItem item2 : order.cart.getItems()) {
               summarizePriceMatrixes(item1, item2);
            }
        }
        
        for(CartItem item1 : order.cart.getItems()) {
           for(CartItem item2 : order.cart.getItems()) {
               summarizePmsAddonItems(item1, item2);
            }
        }
        
        for(CartItem item : order.cart.getItems()) {
            if(item.getTotalAmount() == 0.0) {
                toRemove.add(item.getCartItemId());
            }
        }
        
        for(String removeItem : toRemove) {
            order.cart.removeItem(removeItem);
        }
        
        return this.order;
    }

    private boolean isExactOposite(CartItem item, CartItem item2) {
        if(!item2.getProduct().id.equals(item.getProduct().id)) {
            return false;
        }
        
        if(item2.priceMatrix == null) {
            item2.priceMatrix = new HashMap();
        }
        if(item.priceMatrix == null) {
            item.priceMatrix = new HashMap();
        }
        if(item.itemsAdded == null) {
            item.itemsAdded = new ArrayList();
        }
        if(item2.itemsAdded == null) {
            item2.itemsAdded = new ArrayList();
        }
        
        for(String offset : item.priceMatrix.keySet()) {
            if(!item2.priceMatrix.containsKey(offset)) {
                return false;
            }
        }
        
        if(item.priceMatrix.keySet().size() != item2.priceMatrix.keySet().size()) {
            return false;
        }
        
        List<String> addonList = new ArrayList();
        for(PmsBookingAddonItem addonitem : item.itemsAdded) {
           String key = addonitem.productId + "_" + (addonitem.price * addonitem.count);
           addonList.add(key);
        }
        for(PmsBookingAddonItem addonitem : item2.itemsAdded) {
           String key = addonitem.productId + "_" + (addonitem.price * addonitem.count * -1);
           if(!addonList.contains(key)) {
               return false;
           }
        }
        
        if(item.itemsAdded.size() != item2.itemsAdded.size()) {
            return false;
        }
        
        if(item.getTotalAmount() != (item2.getTotalAmount()*-1)) {
            return false;
        }
        
        return true;
    }

    private void summarizePriceMatrixes(CartItem baseItem, CartItem secondItem) {
        if(baseItem.getCartItemId().equals(secondItem.getCartItemId())) {
            return;
        }
        
        if(baseItem.getCount() > 0 && secondItem.getCount() > 0) {
            return;
        }
        if(baseItem.getCount() < 0) {
            return;
        }
        
        if(baseItem.priceMatrix.isEmpty() || secondItem.priceMatrix.isEmpty()) {
            return;
        }
        
        HashMap<String, Double> removeOffsets = new HashMap();
        for(String offset : baseItem.priceMatrix.keySet()) {
            if(secondItem.priceMatrix.containsKey(offset)) {
                Double toRemove = secondItem.priceMatrix.get(offset);
                Double base = baseItem.priceMatrix.get(offset);
                Double newAmount = base + toRemove;
                removeOffsets.put(offset, newAmount);
                
            }
        }
        
        int countRemovedBase = 0;
        int countRemovedSecond = 0;
        for(String newOffset : removeOffsets.keySet()) {
            if(removeOffsets.get(newOffset) == 0.0) {
                baseItem.priceMatrix.remove(newOffset);
                countRemovedBase++;
            } else {
                baseItem.priceMatrix.put(newOffset, removeOffsets.get(newOffset));
            }
            secondItem.priceMatrix.remove(newOffset);
            countRemovedSecond++;
        }
        
        Double total = baseItem.getPriceMatrixAmount();
        baseItem.setCount(baseItem.getCount()-countRemovedBase);
        Double avg = total / baseItem.getCount();
        baseItem.getProduct().price = avg;
        
        total = secondItem.getPriceMatrixAmount();
        secondItem.setCount(secondItem.getCount()-countRemovedSecond);
        if(secondItem.getCount() != 0) {
            secondItem.getProduct().price = 0;
        } else {
            avg = total / secondItem.getCount();
            secondItem.getProduct().price = avg;
        }
        
        Date startDate = null;
        for(String offset : baseItem.priceMatrix.keySet()) {
            Date tmpDate = PmsBookingRooms.convertOffsetToDate(offset);
            if(startDate == null || tmpDate.before(startDate)) {
                startDate = tmpDate;
            }
        }
        
        if(startDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.HOUR_OF_DAY, 15);
            baseItem.startDate = cal.getTime();
        }
        
        Date endDate = null;
        for(String offset : baseItem.priceMatrix.keySet()) {
            Date tmpDate = PmsBookingRooms.convertOffsetToDate(offset);
            if(endDate == null || tmpDate.after(endDate)) {
                endDate = tmpDate;
            }
        }
        
        if(endDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.HOUR_OF_DAY, 11);
            baseItem.endDate = cal.getTime();
        }
    }

    private void summarizePmsAddonItems(CartItem item1, CartItem item2) {
    }

}
