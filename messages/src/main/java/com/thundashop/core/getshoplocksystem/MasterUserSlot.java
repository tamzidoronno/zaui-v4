/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    public boolean allCodesAdded = false;
    
    public List<UserSlot> subSlots = new ArrayList();    
    
    /**
     * The slots added to this array has one not
     * yet been updated on the locks
     */
    public List<UserSlot> slotsNotOk = new ArrayList();    

}
