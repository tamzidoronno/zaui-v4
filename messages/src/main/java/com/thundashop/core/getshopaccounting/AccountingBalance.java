/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class AccountingBalance {
    /**
     * The balance until this date will be show.
     * 
     * Example: 
     * $ 5 is accounted for at the 10 january
     * 
     * if this date is set to 10 january it will NOT contain this amount
     * 
     */
    public Date balanceToDate;
    public HashMap<String, Double> balances = new HashMap();
}
