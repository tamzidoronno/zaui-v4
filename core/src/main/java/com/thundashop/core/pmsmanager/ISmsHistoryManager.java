/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;

/**
 * Sms historycontroller.
 */
@GetShopApi
@GetShopMultiLayerSession
public interface ISmsHistoryManager {
    
    @Administrator
    public void generateSmsUsage();
}
