/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.paymentterminalmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

/**
 * The payment terminal manager.<br>
 */
@GetShopApi
public interface IPaymentTerminalManager {
    
    @Administrator
    public void saveSettings(PaymentTerminalSettings settings);
    
    public PaymentTerminalSettings getSetings(Integer offset);
}
