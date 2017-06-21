
package com.thundashop.core.vippsmanager;

public class VippsResponse {
    public class VippsResponseStransctionInfo {
        double amount;
        String status = "";
    }
    
    public Integer orderId;
    public VippsResponseStransctionInfo transactionInfo;
}
