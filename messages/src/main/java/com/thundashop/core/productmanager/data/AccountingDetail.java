/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.productmanager.data;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class AccountingDetail  extends DataCommon implements Comparable<AccountingDetail> {
    public int accountNumber = 0;
    public int taxgroup = -1;
    public int getShopTaxGroup = -1;
    public String description = "";
    public String subaccountid = "";
    public String subaccountvalue = "";
    public boolean isAccomodation = false;

    /**
     * The different types are following:
     * 
     * blank = normal accounts
     * fee = Accounts with type fee will be displayed where its possible to register payments.
     */
    public String type = "";
    
    @Override
    public int compareTo(AccountingDetail u) {
      if (getCreatedAccount() == null || u.getCreatedAccount() == null) {
        return 0;
      }
      return getCreatedAccount().compareTo(u.getCreatedAccount());
    }

    private Integer getCreatedAccount() {
        return accountNumber;
    }
    
}