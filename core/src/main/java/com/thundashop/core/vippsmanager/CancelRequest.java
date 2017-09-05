
package com.thundashop.core.vippsmanager;

public class CancelRequest {
    class MerchantInfo {
        String merchantSerialNumber = "";
    }
    class Transaction {
        Integer amount;
        String transactionText;
    }
    
    public Transaction transaction = new Transaction();
    public MerchantInfo merchantInfo = new MerchantInfo();
}
