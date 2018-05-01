/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.common.Administrator;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author ktonder
 */
public class LockCode implements Serializable {
    public Date addedDate = null;
    public Date removedDate = null;
    
    public Date validFrom = null;
    public Date validTo = null;
    public Date dateSchemaUpdatedOnLock;
    
    public int pincodeSize = 6;
    
    @Administrator
    public String cardId = "";
    
    @Administrator
    public int pinCode = 123456;
    
    public int slotId;
    
    void generateRandomCode(int codeSize) {
        Random rnd = new Random();
        int start = (int)Math.pow(10, (codeSize - 1));
        int stop = start * 9;
        pinCode = start + rnd.nextInt(stop);
    }

    void changeCode(int pinCode, String cardId) {
        this.cardId = cardId;
        this.pinCode = pinCode;
    }
    
}
