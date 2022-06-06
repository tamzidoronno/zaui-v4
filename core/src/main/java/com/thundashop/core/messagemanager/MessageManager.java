package com.thundashop.core.messagemanager;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.GrafanaFeederImpl;
import com.thundashop.core.common.GrafanaManager;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toMap;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toMap;
import static java.lang.Math.min;
import java.util.Calendar;
import static java.util.stream.Collectors.toMap;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toMap;
import static java.lang.Math.min;

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
    
    @Autowired
    private GrafanaManager grafanaManager;
    private Date latestSentErrorNotification;
    
    @Override
    public String sendMail(String to, String toName, String subject, String content, String from, String fromName) {
        if(subject == null || subject.trim().isEmpty()) {
            subject = "No subject";
        }
        if(content == null || content.trim().isEmpty()) {
            logPrint("Sending empty email. Throw exception.");
            logPrintException(new Exception());
            return null;
        }
        
        if(from == null || from.isEmpty()) {
            if(storeManager.getMyStore().configuration != null && storeManager.getMyStore().configuration.emailAdress != null) {
                from = storeManager.getMyStore().configuration.emailAdress;
            }
        }
        
        String res = mailFactory.send(from, to, subject, content);
        feedGrafana(content);
        return res;
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
                
                try(FileOutputStream fos = new FileOutputStream(tmpFile)) {
                    fos.write(data);
                }
                
                files.put(tmpFile, fileName);
            }
            
            return mailFactory.sendWithAttachments(from, to, subject, content, files, true);
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        feedGrafana(content);
        return "";
    }


    private void feedGrafana(String content) {
        HashMap<String, Object> toAdd = new HashMap();
        toAdd.put("emailsize", content.length());
        toAdd.put("storeid", storeId);

        grafanaManager.addPoint("webdata", "email", toAdd);
    }    

    @Override
    public List<String> getCollectedEmails() {
        return collectedEmails.emails;
    }

    
    public void sendInvoiceForOrder(String orderId) {
        Order order = orderManager.getOrderSecure(orderId);
        User user = userManager.getUserById(order.userId);
        if(user == null) {
            return;
        }
        String email = user.emailAddress;
        
        if(order.recieptEmail != null && !order.recieptEmail.isEmpty()) {
            email = order.recieptEmail;
        }
        
        sendInvoiceForOrder(orderId, email, null, null);
    }
    
    public void sendInvoiceForOrder(String id, String email, String subject, String body) {
        Order order = orderManager.getOrderSecure(id);
        User user = userManager.getUserById(order.userId);
        if(user == null) {
            return;
        }
        
        HashMap<String, String> attachments = new HashMap();
        attachInvioce(attachments, id);
        String title = "Receipt for payment";
        if (subject != null)
            title = subject;
        
        String message = "Attached you will find your receipt for the payment for order id: " + order.incrementOrderId + ", amount: " + order.cart.getTotal(false);
        
        if (body != null)
            message = body;
        
        String name = user.fullName;
        String copyadress = storeManager.getMyStore().configuration.emailAdress;
        sendMailWithAttachments(email, name, title, message, copyadress, copyadress, attachments);
        feedGrafana(message);
    }
    
    private void attachInvioce(HashMap<String, String> attachments, String orderId) {
        String invoice = invoiceManager.getBase64EncodedInvoice(orderId);
        if (invoice != null && !invoice.isEmpty()) {
            attachments.put("receipt.pdf", invoice);
        }
    }

    @Deprecated // TODO: this method will replaced with logback.error
    public void sendErrorNotify(String inText) {
        if(latestSentErrorNotification != null) {
            Calendar check = Calendar.getInstance();
            check.add(Calendar.HOUR_OF_DAY, -1);
            if(check.getTime().before(latestSentErrorNotification)) {
                return;
            }
        }
        latestSentErrorNotification = new Date();
        sendErrorNotification(inText, null);
    }

    @Deprecated // TODO: this method will replaced with logback.error
    public void sendErrorNotification(String inText, Exception ex) {
        sendErrorNotificationToEmail("post@getshop.com", inText, ex);
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
        String res = "";
        
        if(message == null || message.trim().isEmpty()) {
            logPrint("Tried to send empty sms to :" + to + " using: " + provider);
            return "";
        }
        
        String mainSmsNumber = getStoreSettingsApplicationKey("mainsmsnumber");
        if (mainSmsNumber != null && !mainSmsNumber.isEmpty()) {
            from = mainSmsNumber;
        }
        
        User user = userManager.getUserByCellphone(to);
        if (user != null && user.smsDisabled) {
            return "";
        }
        
        if (provider.equals("plivo")) {
            handler = new PlivoSmsHandler(storeId, database, prefix, from, to, message, frameworkConfig.productionMode);
        }
        
        if (provider.equals("clickatell")) {
            handler = new ClickatellSmsHandler(storeId, database, prefix, from, to, message, frameworkConfig.productionMode);
        }
        
        if (provider.equals("sveve")) {
            handler = new TeleTopiaSmsHandler(storeId, database, prefix, from, to, message, frameworkConfig.productionMode);
        }
        
        if (provider.equals("nexmo")) {
            handler = new TeleTopiaSmsHandler(storeId, database, prefix, from, to, message, frameworkConfig.productionMode);
        }
        
        if (provider.equals("clickatelltuningfiles")) {
            handler = new ClickatellSmsHandlerTuningfiles(storeId, database, prefix, from, to, message, frameworkConfig.productionMode);
        }
        
        if (handler != null) {
            handler.sendMessage();
            res = handler.getMessageId();
        }
        
        feedGrafanaSms(message);
        
        return res;
    }

    private void feedGrafanaSms(String message) {
        HashMap<String, Object> toAdd = new HashMap();
        
        double smses = ((double)message.length() / 160);
        Number smsCount = Math.ceil(smses);
        toAdd.put("smses", smsCount);
        toAdd.put("storeid", (String)storeId);
        
        grafanaManager.addPoint("webdata", "sms", toAdd);
    }

    public void sendJomresMessageToStoreOwner(String message, String subject){
        String emailMessage =  message.replace("\n", "<br />");
        String fromEmail = "post@getshop.com";
        String toEmail =getStoreEmailAddress();

        mailFactory.send(fromEmail, toEmail, subject, emailMessage);
    }
    
    @Override
    public void sendMessageToStoreOwner(String message, String subject) {
        String email = getStoreEmailAddress();
        mailFactory.send(email, email, subject, message);
    }

    @Override
    public List<MailMessage> getMailSent(Date from, Date to, String toEmailAddress) {
        
        BasicDBObject query = new BasicDBObject();
        query.put("className", MailMessage.class.getCanonicalName());
        
        if (toEmailAddress != null && !toEmailAddress.isEmpty()) {
            query.put("to", Pattern.compile(".*"+toEmailAddress+".*" , Pattern.CASE_INSENSITIVE));
        }
        
//        query.put("rowCreatedDate", BasicDBObjectBuilder.start("$gte", from).add("$lte", to).get());
//        query.put("rowCreatedDate", BasicDBObjectBuilder.start("$gte", from).add("$lte", to).get());

        List<DataCommon> datas = database.query(MessageManager.class.getSimpleName(), storeId+"_log", query);
        List<MailMessage> messages = new ArrayList();
        
        for (DataCommon dataCommon : datas) {
            if (dataCommon instanceof MailMessage) {
                messages.add((MailMessage)dataCommon);
            }
        }
        
        Collections.sort(messages, (o1, o2) -> {
            return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
        });
        
        return messages;
    }

    @Override
    public List<SmsMessage> getAllSmsMessages(Date start, Date end) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", SmsMessage.class.getCanonicalName());
        query.put("rowCreatedDate", BasicDBObjectBuilder.start("$gte", start).add("$lte", end).get());

        List<DataCommon> datas = database.query(MessageManager.class.getSimpleName(), storeId+"_log", query);
        ArrayList result = new ArrayList(datas);
        
        Collections.sort(result, new Comparator<DataCommon>(){
             public int compare(DataCommon o1, DataCommon o2){
                 return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
             }
        });
        
        return result;
    }

    @Override
    public List<SmsMessage> getSmsMessagesSentTo(String prefix, String phoneNumber, Date fromDate, Date toDate) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", SmsMessage.class.getCanonicalName());
        query.put("to", phoneNumber);
        query.put("prefix", prefix);
        
        if (fromDate != null && toDate != null) {
            query.put("rowCreatedDate", BasicDBObjectBuilder.start("$gte", fromDate).add("$lte", toDate).get());
        }

        List<DataCommon> datas = database.query(MessageManager.class.getSimpleName(), storeId+"_log", query);
        ArrayList result = new ArrayList(datas);
        
        Collections.sort(result, new Comparator<DataCommon>(){
             public int compare(DataCommon o1, DataCommon o2){
                 return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
             }
        });
        
        return result;
    }

    @Override
    public void saveIncomingMessage(SmsMessage smsMessage, String code) {
        
        if (!code.equals("asodifj2oi4alksfdlan234kjnaksfdnj2jk41hk34l1h1")) {
            throw new ErrorException(26);
        }
        
        smsMessage.outGoing = false;
        smsMessage.rowCreatedDate = new Date();
        
        database.save(MessageManager.class.getSimpleName(), "col_"+storeId+"_log", smsMessage);
    }

    @Override
    public SmsMessagePage getIncomingMessages(int pageNumber) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", SmsMessage.class.getCanonicalName());
        query.put("outGoing", false);
        
        List<DataCommon> datas = database.query(MessageManager.class.getSimpleName(), storeId+"_log", query);
        ArrayList result = new ArrayList(datas);
        
        Collections.sort(result, new Comparator<DataCommon>(){
             public int compare(DataCommon o1, DataCommon o2){
                 return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
             }
        });
        
        Map<Integer, List<SmsMessage>> mappedResult = partition(result, 50);
        
        SmsMessagePage page = new SmsMessagePage();
        page.maxPages = mappedResult.size();
        page.pageNumber = pageNumber;
        page.pageSize = 50;
        page.messages = mappedResult.get(pageNumber);
        
        return page;
    }
    
    private Map<Integer, List<SmsMessage>> partition(List<SmsMessage> list, int pageSize) {
        return IntStream.iterate(0, i -> i + pageSize)
              .limit((list.size() + pageSize - 1) / pageSize)
              .boxed()
              .collect(toMap(i -> i / pageSize,
                             i -> list.subList(i, min(i + pageSize, list.size()))));
    }

    @Deprecated // TODO: this method will replaced with logback.error
    public void sendErrorNotificationToEmail(String email, String inText, Exception ex) {
        String text = "";
        text += "<br/><b>Message:</b> <br/>";
        text += inText.replace("\n", "<br/>");
        text += "<br><br>Store id: " + storeId;
        text += "<br/>Date: " + new Date();
        text += "<br/>Store email: " + getStoreEmailAddress();
        text += "<br/>Store name: " + getStoreName();
        text += "<br/>Store default address: " + getStoreDefaultAddress();
        text += "<br/>Store default login: " + getStoreDefaultAddress() + "/login.php";
        text += "<br/>";
        
        
        if (ex != null) {
            text += "<br/>";
            text += "<br/> <b> Stacktrace: </b>";
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            text += "<br/>   " + sw.toString().replace("\n", "<br/>");
            text += "<br/>";
        }
        
        sendMail(email, email, "Error Notification (" + getStoreDefaultAddress() +")", text, email, email);
 
    }

}