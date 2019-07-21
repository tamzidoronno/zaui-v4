/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.List;
import java.util.Map;
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
    
    /**
     * If this should be created based on a cartitem from a tab.
     */
    public String cartItemId = "";

    public String getKey() {
        return createOrderOnProductId + ";" + addonId;
    }
}
