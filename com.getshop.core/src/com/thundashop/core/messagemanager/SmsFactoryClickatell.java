/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.StoreComponent;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.messagehandler.data.Message;
import com.thundashop.core.storemanager.StorePool;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component("SmsFactoryClickatell")
@Scope("prototype")
public class SmsFactoryClickatell extends StoreComponent implements SMSFactory, Runnable {
    @Autowired
    public Logger logger;
    
    private String message;
    private String to;
    private String from;
    
    private Credentials credentials;

    @Autowired
    public Database database;
    
    @Autowired
    public FrameworkConfig frameworkConfig;
    
    @Autowired
    public DatabaseSaver databaseSaver;

    @Autowired
    public StorePool storeManager;
    
    @Autowired
    public MessageStatusFactory messageStatusFactory;
    
    @Autowired
    public SmsConfiguration config;
    private String messageId;
    
    public SmsFactoryClickatell() {
        credentials = new Credentials(MessageManager.class);
        credentials.manangerName = "MessageManager";
        credentials.storeid = storeId;
    }
    
    @Override
    public String send(String from, String to, String message) {
        SmsFactoryClickatell impl = new SmsFactoryClickatell();
        impl.from = from;
        impl.messageId = UUID.randomUUID().toString();
        impl.to = to;
        impl.message = message;
        impl.databaseSaver = databaseSaver;
        impl.credentials = credentials;
        impl.database = database;
        impl.logger = logger;
        impl.frameworkConfig = frameworkConfig;
        impl.storeManager = storeManager;
        impl.config = config;
        impl.messageStatusFactory = messageStatusFactory;
        impl.setStoreId(storeId);
        messageStatusFactory.logStatus(impl.messageId, "QUEUED", "The message is put on queue", storeId, "SMSs");
        new Thread(impl).start();
        
        return impl.messageId;
    }
    
    private void saveMessageSent() throws ErrorException {
        Message message = new Message();
        message.from = from;
        message.to = to;
        message.type = Message.Type.SMS;
        message.content = this.message;
        message.storeId = storeId;
        databaseSaver.saveObject(message, credentials);
    }
    
    public void run() {
        System.out.println("Sending with clickatell");
        messageStatusFactory.logStatus(messageId, "SENDING", "The message is being put on the mailserver", storeId, "SMS");
        
        try {
            config.setup(storeId);
        } catch (ErrorException ex) {
            logger.error(this, "Could not fetch the configuration for the store.", ex);
            return;
        }
        
        if (storeId == null || storeId.equals("")) {
            logger.error(this, "Could not send sms, the storeId is empty for the component.");
            return;
        }
        
        if (to != null && to.length() > 1 && to.startsWith("0")) {
            to = to.substring(1);
        }
        
        try {
            message = URLEncoder.encode(message, "ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(SmsFactoryClickatell.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String urlString = "http://api.clickatell.com/http/sendmsg?user="+config.getUsername()+"&password="+config.getPassword()+"&api_id="+config.getApiId()+"&concat=3&to="+config.getNumberprefix()+to+"&"+"&text="+message;
        
        if (!frameworkConfig.productionMode) {
            messageStatusFactory.logStatus(messageId, "STOPPED_FRAMEWORK_IN_DEBUG_MODE", "Message was not delivered, the framework is set to debug mode.", storeId, "SMS");
            System.out.println("Url for sms: " + urlString);
            System.out.println("Sent SMS [ to: " + to + ", from: " + from +", Message: " + message + " ]");
            return;
        }
        
        if (to == null) {
            return;
        }
        
        to = to.replace("+", "");
        
        URL url;
        InputStream is = null;
        DataInputStream dis;
        try {
            message = URLEncoder.encode(message, "ISO-8859-1");
//            String urlString = "http://api.clickatell.com/http/sendmsg?user=boggibill&password=RKCDcOSAECbKeY&from=ProMeister&api_id=3492637&to=47"+to+"&text="+message;
            
            url = new URL(urlString);
            dis = new DataInputStream(new BufferedInputStream(is));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String content = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                content += inputLine;
            in.close();
            
            
            System.out.println(content);
            if(!content.trim().startsWith("ID:")) {
                logger.error(this, "Could not send sms to " + to + " from " + from + " message: " + message);
                System.out.println(content);
                messageStatusFactory.logStatus(messageId, "FAILED_TO_SEND", "Message was not delivered. " + content, storeId, "SMS");
                return;
            }
            
            messageStatusFactory.logStatus(messageId, "DELIVERED", "Message was successfully delivered", storeId, "SMS");
        } catch (IOException ex) {
            messageStatusFactory.logStatus(messageId, "FAILED_TO_SEND", "Server refused to accept the message", storeId, ex, "SMS");
            logger.error(this, "Could not send sms to " + to + " from " + from + " message: " + message, ex);
            return;
        } finally {
            try { 
                if (is != null)
                    is.close(); 
            } catch (IOException ioe) {}
        }
        
        try {
            saveMessageSent();
        } catch (ErrorException ex) {
            logger.error(this, "Was not able to save sent sms message", ex);
        }
    }

    @Override
    public int messageCount(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1, 23, 59, 59);
        cal.add(Calendar.HOUR, -24);
        Date stopDate = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        cal.add(Calendar.HOUR, 23);
        cal.add(Calendar.MINUTE, 59);
        Date startDate = cal.getTime();
        
        Credentials credentials = new Credentials(MessageManager.class);
        credentials.storeid = storeId;
        credentials.manangerName = "MessageManager";
                
        int count = 0;
        List<DataCommon> messages = database.retreiveData(credentials);
        for (DataCommon msg : messages) {
            if (msg instanceof Message) {
                Message sendtMessage = (Message)msg;
                if (sendtMessage.type.equals(Message.Type.SMS) 
                        && sendtMessage.rowCreatedDate.after(startDate) 
                        && sendtMessage.rowCreatedDate.before(stopDate)) 
                {
                    count++;
                }
            }
        }
        
        return count;
    }
}