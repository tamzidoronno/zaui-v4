package com.thundashop.core.system;


import com.thundashop.core.common.DataCommon;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ktonder
 */
public class GetShopSystem extends DataCommon {
    public String systemName = "";
    public String serverVpnIpAddress = "";
    public String webAddresses = "";
    public String companyId = "";
    public String remoteStoreId = "";
    
    public Date activeFrom;
    public Date activeTo;
    
    public double monthlyPrice = 0;
    public String productId = "";
    
    /**
     * This represent the date where the system 
     * has been invoiced to.
     */
    public Date invoicedTo;
    
    /**
     * When the invoicedTo has passed, how many months should be 
     * craeted new orders for.
     */
    public int numberOfMonthsToInvoice = 1;

    public boolean isFinalInvoiced() {
        if (activeTo == null)
            return false;
        
        if (invoicedTo == null)
            return false;
        
        return activeTo.after(invoicedTo) || invoicedTo.after(activeTo);
    }
}