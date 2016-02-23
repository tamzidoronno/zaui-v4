package com.thundashop.core.messagemanager;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.thundashop.core.chatmanager.SubscribedToAirgram;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 */
@Component
@GetShopSession
public class MessageManager extends ManagerBase implements IMessageManager {

    @Autowired
    public MailFactory mailFactory;
    
    private CollectedEmails collectedEmails = new CollectedEmails();
    private List<SmsLogEntry> smsLogEntries = new ArrayList();

    private SMSFactory smsFactory;
    
    @Autowired
    private Database database;
    
    @Autowired
    private FrameworkConfig frameworkConfig;
    
    @Autowired
    private StoreManager storeManager;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private InvoiceManager invoiceManager;
    
    @Override
    public String sendMail(String to, String toName, String subject, String content, String from, String fromName) {
        return mailFactory.send(from, to, subject, content);
    }

    @Override
    public int getSmsCount(int year, int month) {
        return 0;
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof CollectedEmails) {
                collectedEmails = (CollectedEmails) dataCommon;
            }
            if(dataCommon instanceof SmsLogEntry) {
                smsLogEntries.add((SmsLogEntry)dataCommon);
            }
        }
    }

    public String sendSms(String provider, String to, String message, String prefix) {
        return sendSmsInternal(provider, to, message, prefix, "");
    }

    public String sendSms(String provider, String to, String message, String prefix, String from) {
        return sendSmsInternal(provider, to, message, prefix, from);
    }

    @Override
    public void collectEmail(String email) {
        collectedEmails.emails.add(email);
        saveObject(collectedEmails);
    }

    @Override
    public String sendMailWithAttachments(String to, String toName, String subject, String content, String from, String fromName, HashMap<String,String> attachments) {
        try {
            Map<String, String> files = new HashMap();
            
            for (String fileName : attachments.keySet()) {
                String tmpFile = "/tmp/"+UUID.randomUUID().toString();
                byte[] data = Base64.decodeBase64(attachments.get(fileName));
                
                FileOutputStream fos = new FileOutputStream(tmpFile);
                fos.write(data);
                fos.close();
                
                files.put(tmpFile, fileName);
            }
            
            return mailFactory.sendWithAttachments(from, to, subject, content, files, true);
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }

    @Override
    public List<String> getCollectedEmails() {
        return collectedEmails.emails;
    }

    public void sendInvoiceForOrder(String id) {
        Order order = orderManager.getOrderSecure(id);
        User user = userManager.getUserById(order.userId);
        if(user == null) {
            return;
        }
        
        HashMap<String, String> attachments = new HashMap();
        attachInvioce(attachments, id);
        String title = "Receipt for payment";
        String message = "Attached you will find your reciept for the payment for order id: " + order.incrementOrderId + ", amount: " + order.cart.getTotal(false);
        String name = user.fullName;
        String email = user.emailAddress;
        String copyadress = storeManager.getMyStore().configuration.emailAdress;
        sendMailWithAttachments(email, name, title, message, copyadress, copyadress, attachments);
    }
    
    private void attachInvioce(HashMap<String, String> attachments, String orderId) {
        String invoice = invoiceManager.getBase64EncodedInvoice(orderId);
        if (invoice != null && !invoice.isEmpty()) {
            attachments.put("reciept.pdf", invoice);
        }
    }

    public void sendErrorNotification(String text) {
        text += "<br>";
        text += "Store id: " + storeId + "<bR>";
        
        sendMail("post@getshop.com", "post@getshop.com", "error notification", text, "post@getshop.com", "post@getshop.com");
    }

    public void sendMailWithDefaults(String name, String email, String title, String message) {
        String fromName = "GetShop";
        String from = "post@getshop.com";
        sendMail(email, name, title, message, from, fromName);
    }

    @Override
    public MailMessage getMailMessage(String smsMessageId) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", MailMessage.class.getCanonicalName());
        query.put("_id", smsMessageId);
        
        List<DataCommon> datas = database.query(MessageManager.class.getSimpleName(), storeId+"_log", query);
        if (datas != null && datas.size() > 0) {
            return (MailMessage) datas.get(0);
        }
        
        return null;
    }
    
    @Override
    public SmsMessage getSmsMessage(String mailMessageId) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", SmsMessage.class.getCanonicalName());
        query.put("_id", mailMessageId);
        
        List<DataCommon> datas = database.query(MessageManager.class.getSimpleName(), storeId+"_log", query);
        if (datas != null && datas.size() > 0) {
            return (SmsMessage) datas.get(0);
        }
        
        return null;
    }

    private String sendSmsInternal(String provider, String to, String message, String prefix, String from) {
        SmsHandler handler = null;
        
        if (provider.equals("plivo")) {
            handler = new PlivoSmsHandler(storeId, database, prefix, from, to, message, frameworkConfig.productionMode);
        }
        
        if (provider.equals("clickatell")) {
            handler = new ClickatellSmsHandler(storeId, database, prefix, from, to, message, frameworkConfig.productionMode);
        }
        
        if (provider.equals("sveve")) {
            handler = new SveveSmsHandler(storeId, database, prefix, from, to, message, frameworkConfig.productionMode);
        }
        
        if (provider.equals("nexmo")) {
            handler = new NexmoSmsHandler(storeId, database, prefix, from, to, message, frameworkConfig.productionMode);
        }
        
        if (handler != null) {
            handler.sendMessage();
            return handler.getMessageId();
        }
        
        return "";
    }
}