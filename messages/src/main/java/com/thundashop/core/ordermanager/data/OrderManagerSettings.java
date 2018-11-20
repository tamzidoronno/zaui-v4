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
    public Date closedTilPeriode = new Date(0);
}
