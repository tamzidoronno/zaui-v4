/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pga;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.Date;

/**
 *
 * @author ktonder
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPgaManager {
    public boolean checkLogin(String token);
    public boolean isLoggedIn();
    
    public PgaResult changeCheckoutDate(Date newDate);
    public PgaRoom getMyRoom();
    
    @Administrator
    public void saveSettings(PgaSettings pgaSettings);
    
    public PgaSettings getSettings();
    
    public PgaResult buyLateCheckout();
    
    public PgaResult buyExtraCleaning(Date date);
}