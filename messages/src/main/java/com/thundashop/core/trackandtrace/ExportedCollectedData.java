/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class ExportedCollectedData extends DataCommon {
    public String PODBarcodeId;
    public String CustomerNumber;
    public String routeId;
    public String tntRouteId = "";
    public String driverId;
    public Date pickupDateTime;
    public double subTotal = 0D;
    public double returnAmt = 0D;
    public double adjustmentAmt = 0D;
    public double prevInvoiceAmtOriginal = 0D;
    public double prevInvoiceAmtEntered = 0D;
    public double creditAmt = 0D;
    public double paymentAmtToCollect = 0D;
    public double cashAmt = 0D;
    public double checkAmt = 0D;
    public String checkNo = "";
    public boolean isCod = false;
    public boolean isCos = false;
    public boolean isOptional = false;
    public int stopNo = 0;
    
    public long tntId = 0;
}
