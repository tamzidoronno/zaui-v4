/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.paymentmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

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
    
    public String agioAccount = "8060"; //Gevinst
    public String dissAgioAccount = "8160"; //Tap
    public String conversionAccount = "7746";
    
    public List<String> multiplePaymentsActive = new ArrayList();

}
