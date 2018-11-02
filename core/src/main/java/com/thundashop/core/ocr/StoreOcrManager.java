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
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import java.util.Date;
import java.util.List;
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
    
    @Autowired
    OrderManager orderManager;
    
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
            //001188671
            return;
        }
        account.accountId = id;
        saveObject(account);
    }

    @Override
    public void checkForPayments() {
        ocrManager.scanOcrFiles();
        List<OcrFileLines> newlines = ocrManager.getNewOcrLines(account.accountId);
        for(OcrFileLines line : newlines) {
            System.out.println("Need to mark payment for kid: " + line.getKid() + " amount: " + line.getAmountInDouble());
            Order toMatch = orderManager.getOrderByKid(line.getKid());
            if(toMatch != null) {
                orderManager.markAsPaidWithTransactionType(toMatch.id, new Date(), line.getAmountInDouble(), Order.OrderTransactionType.OCR);
                line.setMatchOnOrderId(toMatch.incrementOrderId);
            }
        }
        account.lines.addAll(newlines);
        saveObject(account);
        
    }

    @Override
    public String getAccountingId() {
        return account.accountId;
    }

    @Override
    public List<OcrFileLines> getAllTransactions() {
        checkForPayments();
        return account.lines;
    }
}
