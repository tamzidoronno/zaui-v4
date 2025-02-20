/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author boggi
 */
@GetShopApi
public interface IStoreOcrManager {
    @Administrator
    public void setAccountId(String id, String password);
    @Administrator
    public void disconnectAccountId(String password);
    @Administrator
    public void checkForPayments();
    @Administrator
    public String getAccountingId();
    @Administrator
    public List<OcrFileLines> getAllTransactions();
    
    @Administrator
    public boolean isActivated();
    
    @Administrator
    public void retryMatchOrders();
    
    @Administrator
    public List<OcrFileLines> getOcrLinesForDay(int year, int month, int day);
}
