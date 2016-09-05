package com.thundashop.core.bambora;

import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
import java.io.Serializable;
import java.util.ArrayList;

public class BamboraData implements Serializable {
    class CallBackUrl {
        public String url;
    }
    public BamboraCustomer customer = new BamboraCustomer();
    public long instantcaptureamount;
    public BamboraOrder order = new BamboraOrder();
    public BamboraUrls url = new BamboraUrls();

    void setUser(User user) {
        customer.email = user.emailAddress;
        customer.phonenumber = user.cellPhone;
        customer.phonenumbercountrycode = user.prefix;
        order.setAddresses(user);
    }

    void setOrder(Order orderToAdd) {
        order.setOrder(orderToAdd);
    }
    
    void setTotal(Double totalEx, Double total) {
        Integer convertedEx = new Double(totalEx * 100).intValue();
        Integer convertedTotal = new Double(total * 100).intValue();
        order.total = convertedTotal;
        instantcaptureamount = convertedTotal;
        order.vatamount = convertedTotal - convertedEx;
    }

    void setCallbacks(String webAddress, String appId, String orderId, String merchantId, String storeId) {
        
        String callback = "http://pullserver_"+storeId+"_"+merchantId+".nettmannen.no";
        String returnTo = "http://" + webAddress + "/callback.php?app=" + appId + "&orderId=" + orderId + "&nextpage=";
        
        CallBackUrl callbackurl = new CallBackUrl();
        callbackurl.url = callback;
        
        url.accept = returnTo + "payment_success";
        url.decline = returnTo + "payment_failed";
        url.callbacks = new ArrayList();
        url.callbacks.add(callbackurl);
    }
}
