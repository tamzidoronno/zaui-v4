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

    public void changeCartItems(List<ResturantCartItem> cartItems) {
        ArrayList<ResturantCartItem> copied = new ArrayList(cartItems);
        for (ResturantCartItem item : copied) {
            if (!doesCartItemExist(item, this.cartItems)) {
                System.out.println("Item added");
                this.cartItems.add(item);
            }
        }
        
        copied = new ArrayList(this.cartItems);
        for (ResturantCartItem item : copied) {
            if (!doesCartItemExist(item, cartItems)) {
                System.out.println("Item removed");
                this.cartItems.remove(item);
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
    
}
