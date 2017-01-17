/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pdf;

import com.thundashop.core.cartmanager.data.CartItem;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class InvoiceGeneratorCartItem {
    private double price;
    private double count;
    private CartItem cartItem;
    private String metaDataText = "";
    private String name = "";
    private String groupId = "";
    private String productMetaData = "";
    public Integer sequence;

    public InvoiceGeneratorCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
        setValues();
        setAdditionalMetaData();
        setName();
        setProductMetaData();
        this.groupId = cartItem.groupedById;
        
        if (this.groupId == null || this.groupId.isEmpty()) {
            this.groupId = UUID.randomUUID().toString();
        }
    }

    InvoiceGeneratorCartItem(InvoiceGeneratorCartItem firstItem) {
        count = 1;

        name = firstItem.name;
        groupId = firstItem.groupId;
        
        metaDataText = "";
        productMetaData = "";        
        sequence = firstItem.sequence;
        price = firstItem.getTotal();
    }

    private void setValues() {
        this.price = cartItem.getProduct().price;
        this.count = cartItem.getCount();
    }

    public double getPrice() {
        return price;
    }

    public double getCount() {
        return count;
    }

    public double getTotal() {
        return getCount() * getPrice();
    }

    public Date getStartDate() {
        if (cartItem == null)
            return null;
        
        return cartItem.startDate;
    }
    
    public Date getEndDate() {
        if (cartItem == null)
            return null;
        
        return cartItem.endDate;
    }

    private void setAdditionalMetaData() {
        if(cartItem.getProduct() != null && 
                cartItem.getProduct().additionalMetaData != null && 
                !cartItem.getProduct().additionalMetaData.isEmpty()) {
            metaDataText = cartItem.getProduct().additionalMetaData; 
        }
    }

    public String getMetaDataText() {
        return metaDataText;
    }

    public String getName() {
        return name;
    }

    private void setName() {
        this.name = cartItem.getProduct().name;
    }

    public String getProductMetaData() {
        return this.productMetaData;
    }

    private void setProductMetaData() {
        if(cartItem.getProduct() != null && cartItem.getProduct().metaData != null && !cartItem.getProduct().metaData.isEmpty()) {
            this.productMetaData = cartItem.getProduct().metaData;
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public void mergeWith(InvoiceGeneratorCartItem item) {
        price += item.getTotal();
        name += ","  + item.name;
    }   

    public void setGroupedBy(String groupedById) {
        this.groupId = groupedById;
    }
}