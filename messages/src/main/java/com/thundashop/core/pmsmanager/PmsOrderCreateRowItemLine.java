/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;

/**
 *
 * @author ktonder
 */
public class PmsOrderCreateRowItemLine {
    public String createOrderOnProductId = "";
    public boolean isAccomocation = false;
    public boolean includedInRoomPrice = false;
    public int count = 0;
    public double price = 0D;
    public String date = "";
    String textOnOrder = "";
    String addonId = "";
    public String orderItemType = "";
    
    /**
     * If this should be created based on a cartitem from a tab.
     */
    public String cartItemId = "";

    public String getKey() {
        return createOrderOnProductId + ";" + addonId;
    }

    boolean isWithin(Date start, Date end) {
        if (date == null && date.isEmpty()) {
            return false;
        }
        
        SimpleDateFormat sdf  = new SimpleDateFormat("dd-MM-yyyy");
        
        
        try {
            long startDate = start.getTime();
            long endDate = end.getTime();
            long curDate = sdf.parse(date).getTime();
            return startDate <= curDate && curDate <= endDate;
        } catch (ParseException ex) {
            Logger.getLogger(PmsOrderCreateRowItemLine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
        
    }
}
