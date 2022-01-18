package com.thundashop.core.system;


import com.thundashop.core.common.DataCommon;
import com.thundashop.core.productmanager.data.TaxGroup;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ktonder
 */
public class GetShopSystem extends DataCommon implements Comparable<GetShopSystem> {
    public String systemName = "";
    public String serverVpnIpAddress = "";
    public String webAddresses = "";
    public String companyId = "";
    public String remoteStoreId = "";
    
    public Date activeFrom;
    public Date activeTo;
    
    public double monthlyPrice = 0;
    public String productId = "";
    
    public String note = "";
    
    /**
     * This represent the date where the system 
     * has been invoiced to.
     */
    public Date invoicedTo;
    
    public List<TaxGroup> taxGroupsFromSystem = new ArrayList();
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
    
    
    
      @Override
    public int compareTo(GetShopSystem u) {
      if (u.getCreatedOn() == null ) {
          return (getCreatedOn() == null ) ? 0 : -1;
      }
      if (getCreatedOn() == null) {
          return 1;
      }
      return getCreatedOn().compareTo(u.getCreatedOn());
    }

    private Date getCreatedOn() {
        return activeFrom;
    }
}