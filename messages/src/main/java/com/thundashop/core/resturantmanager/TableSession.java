/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class TableSession extends DataCommon {

    private List<ResturantCartItem> cartItems = new ArrayList();
    public String tableId;
    public String createdByUserId = "";
    public Date started = new Date();
    public Date ended = null;
    
    @Transient
    public User createByUser = null;

    public boolean first = true;
    private List<ResturantCartItem> itemsAdded = new ArrayList();
    private List<ResturantCartItem> itemsRemoved = new ArrayList();
    
    public void changeCartItems(List<ResturantCartItem> cartItems) {
        ArrayList<ResturantCartItem> copied = new ArrayList(cartItems);
        for (ResturantCartItem item : copied) {
            if (!doesCartItemExist(item, this.cartItems)) {
                this.cartItems.add(item);
                this.itemsAdded.add(item);
            }
        }
        
        copied = new ArrayList(this.cartItems);
        for (ResturantCartItem item : copied) {
            if (!doesCartItemExist(item, cartItems)) {
                this.cartItems.remove(item);
                itemsRemoved.add(item);
            }
        }
        
        cartItems.stream().forEach(cartItem -> cartItem.sentToKitchen = true);
    }

    private boolean doesCartItemExist(ResturantCartItem item, List<ResturantCartItem> arrayToCheck) {
        for (ResturantCartItem iitem : arrayToCheck) {
            if (iitem.id.equals(item.id)) {
                return true;
            }
        }
        
        return false;
    }
    
    static Comparator<? super TableSession> getSortingByCreatedDate() {
        return (TableSession o1, TableSession o2) -> {
            return o2.started.compareTo(o1.started);
        };
    }    

    public List<ResturantCartItem> getCartItems() {
        return cartItems;
    }

    public boolean hasItem(String itemId) {
        for (ResturantCartItem item : cartItems) {
            if (item.id.equals(itemId)) {
                return true;
            }
        }
        
        return false;
    }

    public ResturantCartItem fetch(String itemId) {
        for (ResturantCartItem item : cartItems) {
            if (item.id.equals(itemId)) {
                return item;
            }
        }
        
        return null;
    }
    
    public ResturantCartItem fetchAndRemove(String itemId) {
        for (ResturantCartItem item : cartItems) {
            if (item.id.equals(itemId)) {
                cartItems.remove(item);
                return item;
            }
        }
        
        return null;
    }
    
    public void clearStatus() {
        itemsAdded.clear();
        itemsRemoved.clear();
    }

    public List<ResturantCartItem> getItemsAdded() {
        return itemsAdded;
    }

    public List<ResturantCartItem> getItemsRemoved() {
        return itemsRemoved;
    }
}
