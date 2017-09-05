
package com.thundashop.core.vippsmanager;

public class StartTransactionBody {
    class MerchantInfo {
        public String merchantSerialNumber = "";
        public String callBack = "";
    }
    class CustomerInfo {
        public String mobileNumber = "";
    }
    class Transaction {
        public String orderId = "";
        public String refOrderId = "";
        public Integer amount = 0;
        public String transactionText = "";
        public String timeStamp = "";
    }
    
    public Transaction transaction = new Transaction();
    public MerchantInfo merchantInfo = new MerchantInfo();
    public CustomerInfo customerInfo = new CustomerInfo();
}
