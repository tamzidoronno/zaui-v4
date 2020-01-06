/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.paymentmanager;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class GeneralPaymentConfig extends DataCommon {
    public Integer accountingCustomerOffset = 0; 
    public Integer accountingerOrderIdPrefix = 0;
    public String postingDate = "";
    
    public String interimPrePaidAccount = "";
    public String interimPostPaidAccount = ""; //Accrude payments.
    public String paidPostingAccount = "";
}
