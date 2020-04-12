package com.thundashop.core.ordermanager.data;

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
public class OrderManagerSettings extends DataCommon {
    /**
     * If this dato is set you will not be able to manipulate any transactiondata or orders that 
     * has financial records before this date.
     * 
     * Note, its all orders until this date, not including.
     */
    public Date closedTilPeriode = null;
    
    /**
     * If this orderId is higher then the current incrementalOrderId it will
     * uses this one from the very next order created
     */
    public long incrementalOrderId = 0;
    
    /**
     * if this is changed to another number then 0, it will be doing accounting
     * days between the day. Lets say you want to do accounting from 05:00 to 05:00, set this to 5.
     * 
     * Default is 0 that is 00:00 -> 00:00
     */
    public int whatHourOfDayStartADay = 0;
    
    /**
     * If this is activated the system will automatically closes yesterdays data so 
     * its ready to be transferred to PMI, Accounting etc.
     */
    public boolean autoCloseFinancialDataWhenCreatingZReport = false;
    
    /**
     * We will close the bank accounting for register incomes on invoices.
     * 
     * If the bank account is closed the operators should not be able 
     * to register payments within that periode.
     * 
     */
    public Date bankAccountClosedToDate = null;
}
