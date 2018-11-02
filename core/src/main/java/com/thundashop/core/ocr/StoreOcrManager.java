/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.storemanager.StoreManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class StoreOcrManager extends ManagerBase implements IStoreOcrManager {
    
    @Autowired
    OcrManager ocrManager;
    
    OcrAccount account = new OcrAccount();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon com : data.data) {
            if(com instanceof OcrAccount) {
                account = (OcrAccount) com;
            }
        }
    }
    
    @Override
    public void setAccountId(String id, String password) {
        if(!password.equals("fdsafdasfbvdsert")) {
            return;
        }
        account.accountId = id;
        saveObject(account);
    }

    @Override
    public void checkForPayments() {
        ocrManager.scanOcrFiles();
    }

    @Override
    public String getAccountingId() {
        return account.accountId;
    }
    
}
