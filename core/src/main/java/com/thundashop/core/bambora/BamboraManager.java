/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bambora;

import com.braintreegateway.Environment;
import com.getshop.pullserver.PullMessage;
import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.dibs.IDibsManager;
import com.thundashop.core.getshop.GetShopPullService;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.PaymentLog;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class BamboraManager extends ManagerBase implements IBamboraManager  {
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    WebManager webManager;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Autowired
    private GetShopPullService getShopPullService; 
    
    @Autowired
    MessageManager messageManager;

    private static final String BAMBORA_APPLICATION_ID = "a92d56c0-04c7-4b8e-a02d-ed79f020bcca";
    
    @Override
    public void checkForOrdersToCapture() {
        String pollKey = getCallBackId();
        if(pollKey == null) { return; }
        if(frameworkConfig.productionMode) {
            pollKey += "-prod";
        } else {
            pollKey += "-debug";
        }
        try {
            //First check for polls.
            long start = System.currentTimeMillis();
            List<PullMessage> messages = getShopPullService.getMessages(pollKey, storeId);
            Gson gson = new Gson();
            for(PullMessage msg : messages) {
                try {
                    BamboraResponse resp = gson.fromJson(msg.getVariables, BamboraResponse.class);
                    Order order = orderManager.getOrderByincrementOrderId(resp.orderid);
                    order.payment.transactionLog.put(System.currentTimeMillis(), msg.getVariables);
                    boolean valid = true;
                    if(!validCallBack(msg.getVariables)) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Invalid callback from bambora callback: " + msg.id + " : " + msg.getVariables);
                        messageManager.sendErrorNotification("Invalid callback from bambora callback: " + msg.id + " : " + msg.getVariables, null);
                        valid = false;
                    }
                    
                    Double amount = new Double(resp.amount) / 100;
                    Double total = orderManager.getTotalAmount(order);
                    if(!amount.equals(total)) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Amount does not match the order (security problem): " + msg.id + " : " + msg.getVariables);
                        messageManager.sendErrorNotification("Amount does not match the order (security problem): " + msg.id + " : " + msg.getVariables, null);
                        valid = false;
                    }
                    if(valid) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Payment completion (BAMBORA) : " + resp.refrence);
                        order.status = Order.Status.PAYMENT_COMPLETED;
                        order.captured = true;
                        orderManager.saveObject(order);
                    }
//                    getShopPullService.markMessageAsReceived(msg.id, storeId);
                }catch(Exception e) {
                    messageManager.sendErrorNotification("Failed to parse json for bambora callback: " + msg.id + " : " + msg.getVariables, e);
//                    getShopPullService.markMessageAsReceived(msg.id, storeId);
                }
            }
        }catch(Exception e) {
            logPrintException(e);
        }
    }

    public String getCheckoutUrl(String orderId) {
        Order order = orderManager.getOrder(orderId);
        User user = userManager.getUserById(order.userId);
        
        BamboraData data = new BamboraData();
        data.setUser(user);
        data.setOrder(order);
        
        Double totalEx = orderManager.getTotalAmountExTaxes(order);
        Double total = orderManager.getTotalAmount(order);
        data.setTotal(totalEx, total);
        
        String addr = getStoreDefaultAddress();
        
        data.setCallbacks(addr, BAMBORA_APPLICATION_ID, getCallBackId(), storeId, frameworkConfig.productionMode);
        return createCheckoutUrl(data);
    }

    private String getAccessToken() {
        Application bamboraApp = storeApplicationPool.getApplicationWithSecuredSettings(BAMBORA_APPLICATION_ID);
        Setting setting = bamboraApp.settings.get("access_token");
        return setting.value;
    }

    private String getCallBackId() {
        Application bamboraApp = storeApplicationPool.getApplicationWithSecuredSettings(BAMBORA_APPLICATION_ID);
        if(bamboraApp == null) {
            return null;
        }
        Setting setting = bamboraApp.settings.get("callback_id");
        if(setting == null) {
            return null;
        }
        return setting.value;
    }

    private String getMd5() {
        Application bamboraApp = storeApplicationPool.getApplicationWithSecuredSettings(BAMBORA_APPLICATION_ID);
        Setting setting = bamboraApp.settings.get("md5");
        return setting.value;
    }

    private String getSecretToken() {
        Application bamboraApp = storeApplicationPool.getApplicationWithSecuredSettings(BAMBORA_APPLICATION_ID);
        Setting setting = bamboraApp.settings.get("secret_token");
        return setting.value;
    }
    
    private String getMerchantId() {
        Application bamboraApp = storeApplicationPool.getApplicationWithSecuredSettings(BAMBORA_APPLICATION_ID);
        Setting setting = bamboraApp.settings.get("merchant_id");
        return setting.value;
    }

    private String createCheckoutUrl(BamboraData data) {
        String endpoint = "https://api.v1.checkout.bambora.com/checkout";
        String access_token = getAccessToken();
        String secret_token = getSecretToken();
        String merchant_id = getMerchantId();
        
        String tokenToUse = access_token + "@" + merchant_id + ":" + secret_token;
        
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String toPost = gson.toJson(data);
        try {
            String res = webManager.htmlPostBasicAuth(endpoint, toPost, true, "ISO-8859-1", tokenToUse);
            BamboraResponse resp = gson.fromJson(res, BamboraResponse.class);

            Order order = orderManager.getOrderByincrementOrderId(resp.orderid);

            PaymentLog pays = new PaymentLog();
            pays.orderId = order.id;
            pays.transactionPaymentId = String.valueOf(resp.refrence);
            pays.isPaymentInitiated = true;
            pays.paymentTypeId = BAMBORA_APPLICATION_ID;
            pays.paymentResponse = new Gson().toJson(resp);
            orderManager.saveOrderPaymenDetails(pays);

            return resp.url;
        }catch(Exception ex) {
            logPrintException(ex);
            return "";
        }
    }

    private boolean validCallBack(String variables) {
        Gson gson = new Gson();
        Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();
        Map<String, String> newMap = gson.fromJson(variables, typeOfHashMap);
        String toCheck = "";
        String hash = "";
        for(String key : newMap.keySet()) {
            if(key.equals("hash")) {
                hash = newMap.get(key);
                continue;
            }
            toCheck += newMap.get(key);
        }
        toCheck += getMd5();
        
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(toCheck.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            String hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashtext.length() < 32 ){
              hashtext = "0"+hashtext;
            }
            
            if(hashtext.equals(hash)) {
                return true;
            }
        }catch(Exception e) {
            return false;
        }
        return false;
    }

}
