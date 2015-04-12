/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
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
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */

public class SMSFactoryImpl extends StoreComponent implements SMSFactory, Runnable {
    
    private String message;
    private String to;
    private String from;
    
    private Credentials credentials;

    private Logger logger;
    private Database database;
    private FrameworkConfig frameworkConfig;
    private DatabaseSaver databaseSaver;
    private StorePool storeManager;
    private MessageManager messageManager;
    private StoreApplicationPool storeApplicationPool;
    private String username;
    private String apiId;
    private String password;
    private String prefix;

    public SMSFactoryImpl(Logger logger, Database database, FrameworkConfig frameworkConfig, DatabaseSaver databaseSaver, StorePool storeManager, StoreApplicationPool storeApplicationPool, MessageManager messageManager) {
        this.logger = logger;
        this.database = database;
        this.messageManager = messageManager;
        this.frameworkConfig = frameworkConfig;
        this.databaseSaver = databaseSaver;
        this.storeManager = storeManager;
        this.storeApplicationPool = storeApplicationPool;
    }
    
    
    
    public SMSFactoryImpl() {
        credentials = new Credentials(MessageManager.class);
        credentials.manangerName = "MessageManager";
        credentials.storeid = storeId;
    }
    
    @PostConstruct
    public void setStoreId() {
        storeId = storeApplicationPool.storeId;
    }
    
    @Override
    public void send(String from, String to, String message) {
        SMSFactoryImpl impl = new SMSFactoryImpl();
        impl.to = to;
        impl.message = message;
        impl.databaseSaver = databaseSaver;
        impl.credentials = credentials;
        impl.database = database;
        impl.logger = logger;
        impl.frameworkConfig = frameworkConfig;
        impl.storeManager = storeManager;
        impl.storeApplicationPool = storeApplicationPool;
        impl.setStoreId(storeId);
        impl.username = storeApplicationPool.getApplication("12fecb30-4e5c-49d8-aa3b-73f37f0712ee").getSetting("username");
        impl.apiId = storeApplicationPool.getApplication("12fecb30-4e5c-49d8-aa3b-73f37f0712ee").getSetting("apiid");
        impl.password = storeApplicationPool.getApplication("12fecb30-4e5c-49d8-aa3b-73f37f0712ee").getSetting("password");
        impl.prefix = storeApplicationPool.getApplication("12fecb30-4e5c-49d8-aa3b-73f37f0712ee").getSetting("numberprefix");
        impl.from = storeApplicationPool.getApplication("12fecb30-4e5c-49d8-aa3b-73f37f0712ee").getSetting("from");
        impl.messageManager = messageManager;
        
        new Thread(impl).start();
    }
    
    public void run() {
        
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
            String urlString = "http://api.clickatell.com/http/sendmsg?user="+username+"&password="+password+"&callback=3&api_id="+apiId+"&concat=3&to="+prefix+to;
            if(from != null && !from.isEmpty()) {
                urlString += "&from="+from;
            }
            urlString += "&text="+message;
            
            url = new URL(urlString);
            dis = new DataInputStream(new BufferedInputStream(is));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String content = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                content += inputLine;
            in.close();
            
            SmsLogEntry entry = new SmsLogEntry();
            entry.clicatellSenderResponse = content;
            entry.message = message;
            entry.to = to;
            entry.apiId = apiId;
            entry.prefix = prefix;
            
            this.messageManager.saveToLog(entry);
            
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

    @Override
    public void setMessageManager(MessageManager manager) {
        this.messageManager = manager;
    }
}