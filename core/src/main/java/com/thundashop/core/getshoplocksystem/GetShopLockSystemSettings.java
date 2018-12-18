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
public class GetShopLockSystemSettings extends DataCommon {
    private int codeSize = 6;
    
    public int getCodeSize() {
        if (storeId.equals("31e0a6ff-656e-4ef5-8973-945ffae8edd0")) {
            return 4;
        }
        
        return codeSize;
    }

    void setCodeSize(int codeSize) {
        this.codeSize = codeSize;
    }
}
