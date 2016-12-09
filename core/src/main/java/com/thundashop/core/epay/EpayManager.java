package com.thundashop.core.epay;

import com.getshop.pullserver.PullMessage;
import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.getshop.GetShopPullService;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class EpayManager extends ManagerBase implements IEpayManager {
        
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
        
    @Autowired
    private GetShopPullService getShopPullService; 
      
    @Autowired
    MessageManager messageManager;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    UserManager userManager;
    
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
                    LinkedHashMap<String,String> polledResult = gson.fromJson(msg.getVariables, LinkedHashMap.class);
                    EpayResponse resp = gson.fromJson(msg.getVariables, EpayResponse.class);
                    Order order = orderManager.getOrderByincrementOrderId(resp.gsordnr);
                    order.payment.callBackParameters = polledResult;
                    
                    boolean valid = true;
                    if(!validCallBack(msg.getVariables)) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Invalid callback from epay callback: " + msg.id + " : " + msg.getVariables);
                        messageManager.sendErrorNotification("Invalid callback from epay callback: " + msg.id + " : " + msg.getVariables, null);
                        valid = false;
                    }
                    
                    
                    saveCardOnUser(order);
                    
                    Double amount = new Double(resp.amount) / 100;
                    Double total = orderManager.getTotalAmount(order);
                    if(!amount.equals(total)) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Amount does not match the order (security problem): " + msg.id + " : " + msg.getVariables);
                        messageManager.sendErrorNotification("Amount does not match the order (security problem): " + msg.id + " : " + msg.getVariables, null);
                        valid = false;
                    }
                    if(valid) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Payment completion (EPAY) : " + resp.txnid);
                        order.status = Order.Status.PAYMENT_COMPLETED;
                        order.captured = true;
                    }
                    orderManager.saveObject(order);
                }catch(Exception e) {
                    messageManager.sendErrorNotification("Failed to parse json for epay callback: " + msg.id + " : " + msg.getVariables, e);
//                    getShopPullService.markMessageAsReceived(msg.id, storeId);
                    e.printStackTrace();
                }
                getShopPullService.markMessageAsReceived(msg.id, storeId);
            }
        }catch(Exception e) {
            logPrintException(e);
        }
    }
    
    private void saveCardOnUser(Order order) {
        User user = userManager.getUserById(order.userId);
        if(user == null) {
            logPrint("Failed to find user?" + order.userId);
            return;
        }

        String subid = order.payment.callBackParameters.get("subscriptionid");
        if(subid == null || subid.isEmpty()) {
            return;
        }
        
        UserCard card = new UserCard();
        card.card = order.payment.callBackParameters.get("5029205");
        card.savedByVendor = "EPAY";
        card.mask = order.payment.callBackParameters.get("cardno");
        user.savedCards.add(card);
        userManager.saveUser(user);
    }
    
    private String getCallBackId() {
        Application epayApp = storeApplicationPool.getApplicationWithSecuredSettings("8f5d04ca-11d1-4b9d-9642-85aebf774fee");
        if(epayApp == null) {
            return null;
        }
        Setting setting = epayApp.settings.get("merchantid");
        if(setting == null) {
            return null;
        }
        return setting.value;
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
        toCheck += getPassword();
        
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
    
    private String getPassword() {
        Application epayApp = storeApplicationPool.getApplicationWithSecuredSettings("8f5d04ca-11d1-4b9d-9642-85aebf774fee");
        Setting setting = epayApp.settings.get("md5secret");
        return setting.value;
    }    

    public void payWithCard(Order order, UserCard card) {
        
    }
}
