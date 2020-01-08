/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class SerosApiKey extends DataCommon {
    public String serosKeyId = "";
    public String getShopLockGroupId = "";
    public Integer getShopSlotNumber = -1;
    
    public SerosApiKeyResult lastReceivedSerosKey;
}
