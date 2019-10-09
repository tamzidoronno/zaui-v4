/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class LoraDevice {
    public String type = "";
    public int deviceId; 
    public HashMap<Integer, LoraUserSlot> userSlots = new HashMap();
}
