/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.common.Administrator;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class LockCode implements Serializable, Cloneable {
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
   
    void generateNewUniqueCode(int codeSize, List<Integer> codesAlreadyInUse) {
        int i = 0;
        while(true) {
            Random rnd = new Random();
            int start = (int)Math.pow(10, (codeSize - 1));
            int stop = start * 9;
            int tmpcode = start + rnd.nextInt(stop);
            if(!codesAlreadyInUse.contains(tmpcode)) {
                pinCode = tmpcode;
                break;
            }
            i++;
            if(i > 1000) {
                generateRandomCode(codeSize);
                break;
            }
        }
    }
    
    void changeCode(int pinCode, String cardId) {
        this.cardId = cardId;
        this.pinCode = pinCode;
    }

    @Override
    public LockCode clone() {
        try {
            return (LockCode) super.clone(); //To change body of generated methods, choose Tools | Templates.
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(LockCode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int getCodeLength() {
        return String.valueOf(pinCode).length();
    }

    
    
}
