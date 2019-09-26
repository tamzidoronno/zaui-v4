/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.external;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class ExternalEndOfDayTransaction extends DataCommon {
    public String batchId;
    public int accountNumber;
    public Double amount;
    public String cashPointId;
}
