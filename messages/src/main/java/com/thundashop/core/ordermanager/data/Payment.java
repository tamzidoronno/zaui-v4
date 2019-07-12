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
import java.util.Map;

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
    public Map<String, String> metaData = new HashMap();
    public String paymentId = "";
    
    public String readablePaymentType() {
        if(paymentType.contains("\\")) {
            return paymentType.substring(paymentType.indexOf("\\")+1);
        }
        return "";
    }
    
    public boolean isPaymentTypeValid() {
        String type = paymentType;
        return type.contains("\\");
    }
    
    public String getPaymentTypeId() {
        String type = paymentType;
        type = type.replaceAll("ns_", "");
        if(type.isEmpty()) {
            return "";
        }
        type = type.substring(0, type.indexOf("\\"));
        type = type.replace("_", "-");
        return type;
    }
    

    public String getIssuer() {
        for (String log : transactionLog.values()) {
            if (log.contains("Result:") && log.contains("32\n")) {
                return log.split(";")[22];
            }
        }
        
        return "";
    }
    
    public String getTransactionId() {
        for (String log : transactionLog.values()) {
            if (log.contains("Result:") && log.contains("32\n")) {
                return log.split(";")[4];
            }
        }
        
        return "";
    }
    
    public String getCardInfo() {
        for (String log : transactionLog.values()) {
            if (log.contains("Result:") && log.contains("32\n")) {
                return log.split(";")[5];
            }
        }
        
        return "";
    }
}

