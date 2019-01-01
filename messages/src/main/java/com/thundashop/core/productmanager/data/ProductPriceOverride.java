/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class ProductPriceOverride {

    private String id = UUID.randomUUID().toString();

    /**
     * This is the userid of who 
     * actually did the change to the price.
     */
    private String userId;
    
    private Date date = new Date();
    
    private String description;
    
    private double newValue;
    
    private ProductPriceOverrideType type; 
    
    private Date deletedDate = null;
    
    private String deletedByUserId = "";
    
    private int seq = 0;
    
    private String subType;

    public ProductPriceOverride(String userId, String description, double newValue, ProductPriceOverrideType type, String subType) {
        this.userId = userId;
        this.description = description;
        this.newValue = newValue;
        this.type = type;
        this.subType = subType;
    }
    
    public void setSequence(int seq) {
        this.seq = seq;
    }
    
    public ProductPriceOverride() {
    }
    
    public void deleteEntry(String userId) {
        deletedDate = new Date();
        deletedByUserId = userId;
    }
    
    public Date getDate() {
        return date;
    }
    
    public ProductPriceOverrideType getType() {
        return type;
    }

    public Double getNewValue() {
        return newValue;
    }

    public boolean isDeleted() {
        return deletedDate != null;
    }
}

