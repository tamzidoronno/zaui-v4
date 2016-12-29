/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class ResturantCartItem extends DataCommon {
    public String productId = "";
    public int tablePersonNumber = 0;
    public String tableId = "";
    public boolean sentToKitchen;
    public String tableSessionId = "";
    public double discountedPrice = 0;
    public boolean useDiscountedPrice = false;
    public Map<String,String> options = new HashMap();

    public String getVariationId() {
        if (options.isEmpty())
            return "";
        
        return ""+options.hashCode();
    }
}