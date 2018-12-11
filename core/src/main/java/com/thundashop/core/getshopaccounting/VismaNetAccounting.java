/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.ordermanager.data.Order;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Used for API transfer to VISMA NET.
 * 
 * @author ktonder
 */
@Component
@GetShopSession
public class VismaNetAccounting extends AccountingSystemBase {

    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders, Date start, Date end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SystemType getSystemType() {
        return SystemType.VISMA_NET;
    }

    @Override
    boolean isUsingProductTaxCodes() {
        return false;
    }

    @Override
    public String getSystemName() {
        return "VISMA NET";
    }

    @Override
    public void handleDirectTransfer(String orderId) {
        
    }
    
}
