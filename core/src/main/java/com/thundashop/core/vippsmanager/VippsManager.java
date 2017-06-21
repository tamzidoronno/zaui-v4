package com.thundashop.core.vippsmanager;

import com.getshop.pullserver.PullMessage;
import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.getshop.GetShopPullService;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component 
@GetShopSession
public class VippsManager  extends ManagerBase implements IVippsManager {
    public String sslKeyDev = "/home/boggi/Dropbox/Leverandører/Vipps/ssl/dev.key";
    public String sslKeyProd = "/home/boggi/Dropbox/Leverandører/Vipps/ssl/prod.key";
    public String sslCertDev = "/home/boggi/Dropbox/Leverandører/Vipps/ssl/912574784-GetShop-InApp-Test.cer";
    public String sslCertProd = "/home/boggi/Dropbox/Leverandører/Vipps/ssl/912574784-GetShop-InApp-Prod.cer";
    
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
    private boolean sentPollFailed;
    
    public String startMobileRequest(String phoneNumber, String orderId) throws Exception {
        String sslKey = sslKeyDev;
        String sslCert = sslCertDev;
        if(frameworkConfig.productionMode) {
            sslKey = sslKeyProd;
            sslCert = sslCertProd;
        }
        
        Application vippsApp = storeApplicationPool.getApplication("f1c8301d-9900-420a-ad71-98adb44d7475");
        String key = vippsApp.getSetting("merchantid") + "-vippsdev";
        if(frameworkConfig.productionMode) {
            key = vippsApp.getSetting("merchantid") + "-vippsprod";
        }
        
        String callback = "http://pullserver_"+key+"_"+storeId+".nettmannen.no";
        
        Order order = orderManager.getOrder(orderId);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String exportFile = "/tmp/" +UUID.randomUUID().toString() + ".txt";
        
        try{
            PrintWriter writer = new PrintWriter(exportFile, "UTF-8");
            writer.println("{\n" +
            " \"merchantInfo\": {\n" +
            " \"merchantSerialNumber\": \""+vippsApp.getSetting("merchantid")+"\",\n" +
            " \"callBack\": \""+callback+"\"\n" +
            " },\n" +
            " \"customerInfo\": {\n" +
            " \"mobileNumber\": \""+phoneNumber+"\"\n" +
            " },\n" +
            " \"transaction\": {\n" +
            " \"orderId\": \""+order.incrementOrderId+"\",\n" +
            " \"refOrderId\": \""+order.incrementOrderId+"\",\n" +
            " \"amount\": "+orderManager.getTotalAmount(order)*100+",\n" +
            " \"transactionText\": \""+order.id+"\"\n" +
            " }\n" +
            "}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
           // do something
        }
        
        String[] cmd = {
            "/usr/bin/curl", "https://gw.atest.dnbnor.no:1447/vipps/v1/payments", "-sL",
            "--key", sslKey, 
            "--cert", sslCert,  
            "-H", "Content-type: application/json",
            "-H", "X-UserId:" + vippsApp.getSetting("userid"),
            "-H", "Authorization:Secret " + vippsApp.getSetting("auth_secret"),
            "-H", "X-Request-Id:" + vippsApp.getSetting("xrequestid"),
            "-H", "X-TimeStamp:" + sdf.format(new Date()),
            "-H", "X-Source-Address:148.251.15.227",
            "--data","@" + exportFile
        };
        
        
        
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        StringBuilder processOutput = new StringBuilder();

        try (BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));)
        {
            String readLine;

            while ((readLine = processOutputReader.readLine()) != null)
            {
                processOutput.append(readLine + System.lineSeparator());
            }

            process.waitFor();
        }
        order.status = Order.Status.NEEDCOLLECTING;
        orderManager.saveOrderInternal(order);
        
        try {
            File file = new File(exportFile);
            file.delete();
        } catch (Exception x) {
            x.printStackTrace();
        }
        
        String result = processOutput.toString().trim();
        return result;
    }
    
    @Override
    public boolean checkIfOrderHasBeenCompleted(Integer incOrderId) {
        checkForOrdersToCapture();
        return false;
    }
    
    @Override
    public void checkForOrdersToCapture() {
        Gson gson = new Gson();
        
        Application vippsApp = storeApplicationPool.getApplication("f1c8301d-9900-420a-ad71-98adb44d7475");
        if (vippsApp == null) {
            return;
        }
        
        String pollKey = vippsApp.getSetting("merchantid") + "-vippsdev";
        if(frameworkConfig.productionMode) {
            pollKey = vippsApp.getSetting("merchantid") + "-vippsprod";
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

                            Double amount = orderManager.getTotalAmount(order);
                            double toCapture = new Double(amount*100).intValue();
                            if(toCapture != response.transactionInfo.amount || !response.transactionInfo.status.equalsIgnoreCase("reserved")) {
                                order.payment.transactionLog.put(System.currentTimeMillis(), "Amount does not match anymore");
                                messageManager.sendMail("post@getshop.com", "post@getshop.com", "Vipps failed (vipps) amount does not (or different status than reserved) match for order, ", gson.toJson(msg), "post@getshop.com", "post@getshop.com");
                                return;
                            }
                            
                            order.payment.transactionLog.put(System.currentTimeMillis(), "Trying to capture this order");
                            messageManager.sendInvoiceForOrder(order.id);
                            order.status = Order.Status.PAYMENT_COMPLETED;
                            orderManager.saveOrder(order);
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
    public boolean cancelOrder(String orderId) throws Exception {
        String sslKey = sslKeyDev;
        String sslCert = sslCertDev;
        if(frameworkConfig.productionMode) {
            sslKey = sslKeyProd;
            sslCert = sslCertProd;
        }
        
        Application vippsApp = storeApplicationPool.getApplication("f1c8301d-9900-420a-ad71-98adb44d7475");
        Order order = orderManager.getOrder(orderId);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String exportFile = "/tmp/" +UUID.randomUUID().toString() + ".txt";
        
        try{
            PrintWriter writer = new PrintWriter(exportFile, "UTF-8");
            writer.println("{\n" +
            " \"merchantInfo\": {\n" +
            " \"merchantSerialNumber\": \""+vippsApp.getSetting("merchantid")+"\"\n" +
            " },\n" +
            " \"transaction\": {\n" +
            " \"transactionText\": \""+order.id+"\"\n" +
            " }\n" +
            "}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
           // do something
        }
        
        String[] cmd = {
            "/usr/bin/curl",
            "-X", "PUT",
            "https://gw.atest.dnbnor.no:1447/vipps/v1/payments/"+order.incrementOrderId+"/cancel", "-sL",
            "--key", sslKey, 
            "--cert", sslCert,  
            "-H", "Content-type: application/json",
            "-H", "X-UserId:" + vippsApp.getSetting("userid"),
            "-H", "Authorization:Secret " + vippsApp.getSetting("auth_secret"),
            "-H", "X-Request-Id:" + vippsApp.getSetting("xrequestid"),
            "-H", "X-TimeStamp:" + sdf.format(new Date()),
            "-H", "X-Source-Address:148.251.15.227",
            "--data","@" + exportFile
        };
        

        
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        StringBuilder processOutput = new StringBuilder();
        System.out.println("Executing: " + processBuilder.command());

        try (BufferedReader processOutputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));)
        {
            String readLine;

            while ((readLine = processOutputReader.readLine()) != null)
            {
                processOutput.append(readLine + System.lineSeparator());
            }

            process.waitFor();
        }
        order.status = Order.Status.WAITING_FOR_PAYMENT;
        orderManager.saveOrderInternal(order);
        
        try {
            File file = new File(exportFile);
            file.delete();
        } catch (Exception x) {
            x.printStackTrace();
        }

        return true;
    }

    
}
