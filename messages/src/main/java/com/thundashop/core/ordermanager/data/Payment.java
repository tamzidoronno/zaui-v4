/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.thundashop.core.productmanager.data.TaxGroup;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Payment implements Serializable {
    public String paymentType = "";
    public double paymentFee = 0D;
    public TaxGroup paymentFeeTaxGroup = null;
    public HashMap<Long, String> transactionLog = new HashMap();
    public List<Date> triedAutoPay = new ArrayList();
    public boolean captured = false;
    public HashMap<String, String> callBackParameters = new HashMap();
    public String paymentId = "";

    public String readablePaymentType() {
        if(paymentType.contains("\\")) {
            return paymentType.substring(paymentType.indexOf("\\")+1);
        }
        return "";
    }
    
    public String getPaymentTypeId() {
        String type = paymentType;
        type = type.replaceAll("ns_", "");
        type = type.substring(0, type.indexOf("\\"));
        type = type.replace("_", "-");
        return type;
    }
}

