/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.giftcard;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class GiftCard extends DataCommon {
    public String createdByUser = "";
    
    public String cardCode = "";
    
    public double giftCardValue = 0D;
    
    public double remainingValue = 0D;
    
    /**
     * Orders used with 
     * this giftcard.
     */
    private List<String> orderIds = new ArrayList();
    
    public String createdByCartItemId;
    
    public String createdByOrderId;
    
    public void addOrder(String orderId, double orderTotalAmount) {
        orderIds.add(orderId);
        remainingValue -= orderTotalAmount;
    }
}
