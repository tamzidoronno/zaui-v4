/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.verifonemanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

/**
 * Handle payments trough verifone.
 */
@GetShopApi
public interface IVerifoneManager {
    @Administrator
    public void chargeOrder(String orderId, Integer terminalNumber);
}
