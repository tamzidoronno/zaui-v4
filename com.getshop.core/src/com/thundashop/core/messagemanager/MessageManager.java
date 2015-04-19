package com.thundashop.core.messagemanager;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.chatmanager.SubscribedToAirgram;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.getshop.data.SmsResponse;
import com.thundashop.core.storemanager.StorePool;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 */
@Component
@GetShopSession
public class MessageManager extends ManagerBase implements IMessageManager {

    @Autowired
    public MailFactory mailFactory;
    
    private SubscribedToAirgram airgramSubscriptions = new SubscribedToAirgram();
    private CollectedEmails collectedEmails = new CollectedEmails();
    private List<SmsLogEntry> smsLogEntries = new ArrayList();

    private SMSFactory smsFactory;
    
    @Autowired
    private Logger logger;
    
    @Autowired
    private Database database;
    
    @Autowired
    private FrameworkConfig frameworkConfig;
    
    @Autowired
    private DatabaseSaver databaseSaver;
    
    @Autowired
    private StorePool storeManager;
    
    @Autowired
    private StoreApplicationPool storeApplicationPool;
    
    @Autowired
    private GetShop getShop;
    
    @PostConstruct
    public void createSmsFactory() {
        smsFactory = new SMSFactoryImpl(logger, database, frameworkConfig, databaseSaver, storeManager, storeApplicationPool, this);
        smsFactory.setStoreId(storeId);
    }

    @Override
    public void sendMail(String to, String toName, String subject, String content, String from, String fromName) {
        mailFactory.send(from, to, subject, content);
    }

    @Override
    public int getSmsCount(int year, int month) {
        return smsFactory.messageCount(year, month);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof SubscribedToAirgram) {
                airgramSubscriptions = (SubscribedToAirgram) dataCommon;
            }
            if (dataCommon instanceof CollectedEmails) {
                collectedEmails = (CollectedEmails) dataCommon;
            }
            if(dataCommon instanceof SmsLogEntry) {
                smsLogEntries.add((SmsLogEntry)dataCommon);
            }
        }
    }

    private void checkIfSubscribed(String account) throws ErrorException {
        if (!airgramSubscriptions.accounts.contains(account)) {
            String url = "http://api.airgramapp.com/1/subscribe";
            String data = "email=" + account;
            sendToAirgramHttp(data, url);
            airgramSubscriptions.accounts.add(account);
            airgramSubscriptions.storeId = storeId;
            databaseSaver.saveObject(airgramSubscriptions, credentials);
        }
    }
  

    private void sendToAirgramHttp(String data, String sendurl) throws ErrorException {
        try {
            String key = "UqxIL7fjTy";
            String secret = "FOnheTh5Lq1vC9hKqOKC";

            URL obj = new URL(sendurl);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            String userpass = key + ":" + secret;
            String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
            conn.setRequestProperty("Authorization", basicAuth);

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(data);
            out.close();

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSms(String to, String message) {
        smsFactory.setMessageManager(this);
        smsFactory.send("", to, message);
    }

    @Override
    public void collectEmail(String email) {
        collectedEmails.emails.add(email);
        saveObject(collectedEmails);
    }

    @Override
    public void sendMailWithAttachments(String to, String toName, String subject, String content, String from, String fromName, HashMap<String,String> attachments) {
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
            
            mailFactory.sendWithAttachments(from, to, subject, content, files, true);
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MessageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void saveToLog(SmsLogEntry entry) {
        entry.storeId = storeId;
        databaseSaver.saveObject(entry, credentials);
        smsLogEntries.add(entry);
    }

    @Override
    public List<SmsLogEntry> getSmsLog() {
        for(SmsLogEntry entry : smsLogEntries) {
            if(entry.status == null && entry.msgId != null) {
                SmsResponse response = getShop.getSmsResponse(entry.msgId);
                if(response != null) {
                    entry.errorCode = response.errCode;
                    entry.delivered = response.deliveryTime;
                    saveObject(entry);
                }
            }
        }
        return smsLogEntries;
    }

}
