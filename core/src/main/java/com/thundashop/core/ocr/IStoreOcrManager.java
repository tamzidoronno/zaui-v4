/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ocr;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

/**
 *
 * @author boggi
 */
@GetShopApi
public interface IStoreOcrManager {
    @Administrator
    public void setAccountId(String id, String password);
    @Administrator
    public void checkForPayments();
    @Administrator
    public String getAccountingId();
}
