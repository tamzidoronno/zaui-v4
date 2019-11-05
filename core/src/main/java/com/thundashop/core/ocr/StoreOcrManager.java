/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.CheckConsistencyCron;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
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
    
    HashMap<String, OcrFileLines> lines = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon com : data.data) {
            if(com instanceof OcrAccount) {
                account = (OcrAccount) com;
            }
            if(com instanceof OcrFileLines) {
                OcrFileLines line = (OcrFileLines) com;
                lines.put(line.id, line);
            }
        }
        
        
        moveLinesFromAccountDirectToManager();
        createScheduler("storeocrprocessor", "20 17,3,5 * * *", OcrProcessor.class);
    }

    /**
     * This function is used for cleaning up a potential problem where the
     * document grows out of size.
     * 
     * @throws ErrorException 
     */
    private void moveLinesFromAccountDirectToManager() throws ErrorException {
        if (account != null && account.hasLines()) {
            saveLines(account.getLines());
            account.clearLines();
            saveObject(account);
        }
    }

    private void saveLines(List<OcrFileLines> inLines) {
        inLines.stream()
                .forEach(line -> {
                    saveObject(line);
                    lines.put(line.id, line);
                });
    }
    
    @Override
    public void setAccountId(String id, String password) {
        if(!password.equals("fdsafdasfbvdsert")) {
            //001188671
            return;
        }
        
        if(id.length() != 9) {
            return;
        }
        
        account.accountId = id;
        saveObject(account);
    }

    @Override
    public void disconnectAccountId(String password) {
        if(!password.equals("fdsafdasfbvdsert")) {
            //001188671
            return;
        }
        account.accountId = null;
        saveObject(account);
    }

    @Override
    public void checkForPayments() {
        ocrManager.scanOcrFiles();
        List<OcrFileLines> newlines = ocrManager.getNewOcrLines(account.accountId);
        for(OcrFileLines line : newlines) {
            Order toMatch = orderManager.getOrderByKid(line.getKid());
            if(toMatch != null) {
                Date paymentDate = line.getPaymentDate();
                orderManager.markAsPaidWithTransactionType(toMatch.id, paymentDate, line.getAmountInDouble(), Order.OrderTransactionType.OCR, line.getOcrLineId());
                line.setMatchOnOrderId(toMatch.incrementOrderId);
            }
        }
        
        saveLines(newlines);
        
        saveObject(account);
    }

    @Override
    public String getAccountingId() {
        return account.accountId;
    }

    @Override
    public List<OcrFileLines> getAllTransactions() {
        checkForPayments();
        return new ArrayList(lines.values());
    }

    @Override
    public boolean isActivated() {
        return !lines.isEmpty();
    }

    @Override
    public List<OcrFileLines> getOcrLinesForDay(int year, int month, int day) {
        return lines.values()
                .stream()
                .filter(o -> o.isForDay(year, month, day))
                .collect(Collectors.toList());
    }
    
    public OcrFileLines getOcrFileLine(String ocrLineId) {
        return lines.values()
                .stream()
                .filter(o -> o.getOcrLineId().equals(ocrLineId))
                .findFirst()
                .orElse(null);
    }
}
