package com.thundashop.core.vippsmanager;

import com.getshop.pullserver.PullMessage;
import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.getshop.GetShopPullService;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.webmanager.WebManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component 
@GetShopSession
public class VippsManager  extends ManagerBase implements IVippsManager {
    
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    GetShopPullService getShopPullService;
    
    @Autowired
    MessageManager messageManager;
    
    @Autowired
    WebManager webManager;
    
    private boolean sentPollFailed;
    
    private String endpointTest = "https://apitest.vipps.no/";
    private String endpoint = "https://api.vipps.no/";
    
    public boolean startMobileRequest(String phoneNumber, String orderId, String ip) throws Exception {
        String token = getToken();
        
        Order order = orderManager.getOrderSecure(orderId);
        
        Application vippsApp = storeApplicationPool.getApplication("f1c8301d-9900-420a-ad71-98adb44d7475");
        String clientId = vippsApp.getSetting("clientid"); 
        String ocp = vippsApp.getSetting("subscriptionkeysecondary");
        String adress = vippsApp.getSetting("adress");
        String serialNumber = vippsApp.getSetting("serialNumber");
        
        String startEndpoint = endpoint;
        
        if(!getProductionMode()) {
            startEndpoint = endpointTest;
        }
        
        String key = serialNumber + "-vippsdev";
        if(getProductionMode()) {
            key = serialNumber + "-vippsprod";
        }
        
        String callback = "http://pullserver_"+key+"_"+storeId+".nettmannen.no";
        String url = startEndpoint + "/Ecomm/v1/payments";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        
        HashMap<String, String> header = new HashMap();
        header.put("Authorization", token);
        header.put("X-Request-Id", order.incrementOrderId + "");
        header.put("X-TimeStamp", sdf.format(new Date()));
        header.put("X-Source-Address", ip);
        header.put("X-App-Id", clientId);
        header.put("Ocp-Apim-Subscription-Key", ocp);
        
        try {
            Gson gson = new Gson();
            
            StartTransactionBody body = new StartTransactionBody();
            body.merchantInfo.merchantSerialNumber = serialNumber;
            body.merchantInfo.callBack = callback;
            
            body.customerInfo.mobileNumber = phoneNumber;
            body.transaction.orderId = order.incrementOrderId + "";
            body.transaction.amount = new Double(orderManager.getTotalAmount(order) * 100).intValue();
            body.transaction.transactionText = "Payment for order: " + order.incrementOrderId;
            body.transaction.timeStamp = sdf.format(new Date());
            String json = gson.toJson(body);
            
            if(printDebugData()) {
                logPrint("------------");
                logPrint("Endpoint: " + url);
                for(String k : header.keySet()) {
                    logPrint(k + " : " + header.get(k));
                }
                logPrint("Json:\n" + json);
                logPrint("------------");
            }

            String res = webManager.htmlPostBasicAuth(url, json, true, "UTF-8", "", "", false, "POST", header);
            if(printDebugData()) {
                logPrint("----");
                logPrint(res);
                logPrint("----");
            }
            StartTransactionResponse response = gson.fromJson(res, StartTransactionResponse.class);
            if(response.transactionInfo.status.equals("RESERVE")) {
                order.status = Order.Status.NEEDCOLLECTING;
            }
            order.payment.transactionLog.put(System.currentTimeMillis(), res);
            orderManager.saveOrder(order);
            return true;
        }catch(Exception e) {
            logPrintException(e);
            return false;
        }
        
    }
    
    @Override
    public boolean checkIfOrderHasBeenCompleted(Integer incOrderId) {
        checkForOrdersToCapture();                        
        return orderManager.getOrderByincrementOrderId(incOrderId).status == Order.Status.PAYMENT_COMPLETED;
    }
    
    @Override
    public void checkForOrdersToCapture() {
        Gson gson = new Gson();
        
        Application vippsApp = storeApplicationPool.getApplication("f1c8301d-9900-420a-ad71-98adb44d7475");
        if (vippsApp == null) {
            return;
        }

        String serialNumber = vippsApp.getSetting("serialNumber");
        
        String pollKey = serialNumber + "-vippsdev";
        if(getProductionMode()) {
            pollKey = serialNumber + "-vippsprod";
        }
        try {
            //First check for polls.
            long start = System.currentTimeMillis();
            List<PullMessage> messages = getShopPullService.getMessages(pollKey, storeId);
            long end = System.currentTimeMillis();
            logPrint(start - end + "  meesagses: " + messages.size());
            for(PullMessage msg : messages) {
                try {
                    VippsResponse response = gson.fromJson(msg.body, VippsResponse.class);
                    if(response.orderId == null || response.orderId == 0) {
                        messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed prosessing message from pull server (vipps)", gson.toJson(msg), "post@getshop.com", "post@getshop.com");
                    } else {
                        Order order = orderManager.getOrderByincrementOrderId(response.orderId);
                        try {
                            order.payment.callBackParameters.put("body", msg.body);
                            if(captureOrder(response, order, msg)) {
                                messageManager.sendInvoiceForOrder(order.id);
                                order.status = Order.Status.PAYMENT_COMPLETED;
                                orderManager.saveOrder(order);
                            } else {
                                messageManager.sendErrorNotification("Failed to capture order for vipps:" + order.incrementOrderId, null);
                                order.status = Order.Status.PAYMENT_FAILED;
                                orderManager.saveOrder(order);
                            }
                        }catch(Exception d) {
                            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed message message from pull server (vipps)", gson.toJson(msg), "post@getshop.com", "post@getshop.com");
                            order.payment.transactionLog.put(System.currentTimeMillis(), "Failed capturing order: " + d.getMessage());
                            logPrintException(d);
                        }
                    }
                }catch(Exception ex) {
                    messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed message message from pull server (vipps)", gson.toJson(msg), "post@getshop.com", "post@getshop.com");
                    logPrintException(ex);
                }
                getShopPullService.markMessageAsReceived(msg.id, storeId);
            }
            if (sentPollFailed) {
                messageManager.sendMail("post@getshop.com", "post@getshop.com", "Pullserver", "Back online again (vipps)", "post@getshop.com", "post@getshop.com");
            }
            sentPollFailed = false;
        } catch (Exception ex) {
            if (!sentPollFailed) {
                messageManager.sendMail("post@getshop.com", "post@getshop.com", "Failed to fetch from pull server (vipps)", "Is pull server down: " + ex.getMessage(), "post@getshop.com", "post@getshop.com");
                sentPollFailed = true;
                logPrintException(ex);
            }
        }
    }

    @Override
    public boolean cancelOrder(String orderId, String ip) throws Exception {
        Application vippsApp = storeApplicationPool.getApplication("f1c8301d-9900-420a-ad71-98adb44d7475");
        Order order = orderManager.getOrder(orderId);
        String token = getToken();
        String clientId = vippsApp.getSetting("clientid"); 
        String ocp = vippsApp.getSetting("subscriptionkeysecondary");
        String serialNumber = vippsApp.getSetting("serialNumber");

        String startEndpoint = endpoint;
        
        if(!getProductionMode()) {
            startEndpoint = endpointTest;
        }
        
        
        String url = startEndpoint + "/Ecomm/v1/payments/"+order.incrementOrderId+"/cancel";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        
        HashMap<String, String> header = new HashMap();
        header.put("Authorization", token);
        header.put("X-TimeStamp", sdf.format(new Date()));
        header.put("X-Source-Address", ip);
        header.put("X-App-Id", clientId);
        header.put("Ocp-Apim-Subscription-Key", ocp);
        
        if(!printDebugData()) {
            logPrint("---------");
            logPrint("Endpoint:" + url);
            for(String k : header.keySet()) {
                logPrint(k + ":" + header.get(k));
            }
            logPrint("---------");
        }
        
        CancelRequest cancelation = new CancelRequest();
        cancelation.merchantInfo.merchantSerialNumber = serialNumber;
        cancelation.transaction.amount = new Double(orderManager.getTotalAmount(order)*100).intValue();
        cancelation.transaction.transactionText = "Cancelled by user";
        
        Gson gson = new Gson();
        String res = webManager.htmlPostBasicAuth(url, gson.toJson(cancelation), true, "UTF-8", "", "", false, "PUT", header);
        if(!printDebugData()) {
            logPrint("---------");
            logPrint(res);
            logPrint("---------");
        }
        
        return true;
    }

    private String getToken() {
        Application vippsApp = storeApplicationPool.getApplication("f1c8301d-9900-420a-ad71-98adb44d7475");
        String clientId = vippsApp.getSetting("clientid"); 
        String secret = vippsApp.getSetting("secret");
        String ocp = vippsApp.getSetting("subscriptionkeyprimary");
        
        String startEndpoint = endpoint;
        
        if(!getProductionMode()) {
            startEndpoint = endpointTest;
        }
        
        String url = startEndpoint + "accessToken/get";
        
        HashMap<String, String> header = new HashMap();
        header.put("client_id", clientId);
        header.put("client_secret", secret);
        header.put("Ocp-Apim-Subscription-Key", ocp);
        
        try {
            
            if(printDebugData()) {
                logPrint("------------");
                logPrint("Endpoint:" + url);
                for(String k : header.keySet()) {
                    logPrint(k + " : " + header.get(k));
                }
                logPrint("------------");
            }
            
            Gson gson = new Gson();
            String res = webManager.htmlPostBasicAuth(url, "test", false, "UTF-8", "", "", false, "POST", header);
            if(printDebugData()) {
                logPrint("----");
                logPrint(res);
                logPrint("----");
            }
            AccessTokenResponse tokeResp = gson.fromJson(res, AccessTokenResponse.class);
            return tokeResp.access_token;
        }catch(Exception e) {
            logPrintException(e);
            return "";
        }
    }

    private boolean captureOrder(VippsResponse response, Order order, PullMessage msg) {
        String token = getToken();
        Application vippsApp = storeApplicationPool.getApplication("f1c8301d-9900-420a-ad71-98adb44d7475");
        Double amount = orderManager.getTotalAmount(order);
        double toCapture = new Double(amount*100).intValue();
        
        Gson gson = new Gson();

        if(toCapture != response.transactionInfo.amount || !response.transactionInfo.status.equalsIgnoreCase("reserved")) {
            order.payment.transactionLog.put(System.currentTimeMillis(), "Amount does not match anymore");
            messageManager.sendMail("post@getshop.com", "post@getshop.com", "Vipps failed (vipps) amount does not (or different status than reserved) match for order, ", gson.toJson(msg), "post@getshop.com", "post@getshop.com");
            return false;
        }
        order.payment.transactionLog.put(System.currentTimeMillis(), "Trying to capture this order");
        
        
        String clientId = vippsApp.getSetting("clientid"); 
        String ocp = vippsApp.getSetting("subscriptionkeysecondary");
        String serialNumber = vippsApp.getSetting("serialNumber");

        String startEndpoint = endpoint;
        
        if(!getProductionMode()) {
            startEndpoint = endpointTest;
        }
        
        
        String url = startEndpoint + "/Ecomm/v1/payments/"+order.incrementOrderId+"/capture";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        
        HashMap<String, String> header = new HashMap();
        header.put("Authorization", token);
        header.put("X-TimeStamp", sdf.format(new Date()));
        header.put("X-Source-Address", "138.68.66.223");
        header.put("X-Request-Id", order.incrementOrderId + "");
        header.put("X-App-Id", clientId);
        header.put("Ocp-Apim-Subscription-Key", ocp);
        
        if(printDebugData()) {
            logPrint("---------");
            logPrint("Endpoint:" + url);
            for(String k : header.keySet()) {
                logPrint(k + ":" + header.get(k));
            }
            logPrint("---------");
        }
        
        CancelRequest cancelation = new CancelRequest();
        cancelation.merchantInfo.merchantSerialNumber = serialNumber;
        cancelation.transaction.amount = new Double(orderManager.getTotalAmount(order)*100).intValue();
        cancelation.transaction.transactionText = "Captured";
        
        try {
            String res = webManager.htmlPostBasicAuth(url, gson.toJson(cancelation), true, "UTF-8", "", "", false, "POST", header);
            if(printDebugData()) {
                logPrint("---------");
                logPrint(res);
                logPrint("---------");
            }
            StartTransactionResponse captureResponse = gson.fromJson(res, StartTransactionResponse.class);
            return captureResponse.transactionInfo.status.equals("Captured");
        }catch(Exception e) {
            return false;
        }
        
    }

    private boolean getProductionMode() {
//        return true;
        return frameworkConfig.productionMode;
    }
    
    private boolean printDebugData() {
        return true;
    }

    
}
