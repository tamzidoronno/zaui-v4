/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.resturantmanager;

import com.thundashop.core.common.DataCommon;

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
}