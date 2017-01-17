/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class PaymentTransaction extends DataCommon {
    public String status = "started";
    public String userId = "";
    public List<ResturantCartItem> cartItems = new ArrayList();
    public String orderId = "";
    
}
