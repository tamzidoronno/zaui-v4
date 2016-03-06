/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class BookingItemTypeMetadata extends DataCommon {
    
    /**
     * Key = groupId
     * Value = true / false / (null = true);
     */
    public HashMap<String, Boolean> visibleForGroup = new HashMap();
    
    /**
     * Key = groupId
     * Value = price
     */
    public HashMap<String, Double> groupPrices = new HashMap();
    
    /**
     * Key = groupId
     * Value = List of certificates for an eventType.
     */
    public HashMap<String, List<String>> certificateIds = new HashMap();
    
    public boolean publicVisible = true;
    
    public Double publicPrice = -1D;
    
    public String bookingItemTypeId = "";
    
    public String questBackId = "";
}
