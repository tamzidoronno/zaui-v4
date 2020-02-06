/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.external.ExternalCartItem;
import com.thundashop.core.external.ExternalPosAccess;
import com.thundashop.core.external.ExternalPosProduct;
import com.thundashop.core.external.ExternalRoom;
import java.util.List;
import javax.xml.ws.soap.Addressing;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IExternalPosManager {
    public boolean hasAccess(String token);
    public List<ExternalPosProduct> getProducts(String token);
    public List<ExternalRoom> getRoomList(String token);
    public String addToRoom(String token, String roomId, ExternalCartItem externalCartItem);
    public void removeTransaction(String token, String addonId);
    
    public void addEndOfDayTransaction(String token, String batchId, int accountNumber, double transactionAmount);
}

