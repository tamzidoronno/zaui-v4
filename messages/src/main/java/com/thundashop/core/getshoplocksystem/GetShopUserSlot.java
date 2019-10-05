/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class GetShopUserSlot implements Serializable {
    public String pinCode;
    public boolean setOnLock = false;
    public boolean removeFromLock = false;
}
