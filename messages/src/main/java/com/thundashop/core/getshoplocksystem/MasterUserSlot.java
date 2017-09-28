/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for Lock Groups.
 * 
 * A MasterUserSlot is a slot on a different lock
 * that should be mirrored to its master.
 *
 *
 * @author ktonder
 */
public class MasterUserSlot extends UserSlot {
    public List<UserSlot> subSlots = new ArrayList();

   
    
}
