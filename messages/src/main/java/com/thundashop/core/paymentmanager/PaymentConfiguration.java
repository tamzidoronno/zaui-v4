/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.paymentmanager;

import com.thundashop.core.common.DataCommon;

/**
 * This object contains the information for a payment method, this object
 * is shared across all the stores.
 * 
 * Do not use this as a personal storage for a store.
 * 
 * @author ktonder
 */
public class PaymentConfiguration extends DataCommon {

    /**
     * Payment Status.
     */
    public boolean isAllowedToManuallyMarkAsPaid = true;
    public boolean automaticallyCloseOrderWhenPaid = false;
    
    /**
     * Accounting settings.
     */
    public boolean transferToAccountingBasedOnCreatedDate = false;
    public boolean transferToAccountingBasedOnPaymentDate = false;
    
    /**
     * Credit notes.
     */
    public boolean transferCreditNoteToAccountingBasedOnCreatedDate = false;
    public boolean transferCreditNoteToAccountingBasedOnPaymentDate = false;
    
}
