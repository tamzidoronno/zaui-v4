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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class SMSFactoryImpl extends StoreComponent implements SMSFactory, Runnable {
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
    public SmsConfiguration config;
    
    public SMSFactoryImpl() {
        credentials = new Credentials(MessageManager.class);
        credentials.manangerName = "MessageManager";
        credentials.storeid = storeId;
    }
    
    @Override
    public void send(String from, String to, String message) {
        SMSFactoryImpl impl = new SMSFactoryImpl();
        impl.from = from;
        impl.to = to;
        impl.message = message;
        impl.databaseSaver = databaseSaver;
        impl.credentials = credentials;
        impl.database = database;
        impl.logger = logger;
        impl.frameworkConfig = frameworkConfig;
        impl.storeManager = storeManager;
        impl.config = config;
        impl.setStoreId(storeId);
        new Thread(impl).start();
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
        
        if (!frameworkConfig.productionMode) {
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
            String urlString = "http://api.clickatell.com/http/sendmsg?user="+config.getUsername()+"&password="+config.getPassword()+"&api_id="+config.getApiId()+"&to="+config.getNumberprefix()+to+"&text="+message;
            url = new URL(urlString);
            dis = new DataInputStream(new BufferedInputStream(is));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String content = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                content += inputLine;
            in.close();
            
            if(!content.trim().startsWith("ID:")) {
                logger.error(this, "Could not send sms to " + to + " from " + from + " message: " + message);
                System.out.println(content);
                return;
            }
        } catch (IOException ex) {
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