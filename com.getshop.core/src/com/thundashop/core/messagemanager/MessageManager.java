package com.thundashop.core.messagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.chatmanager.SubscribedToAirgram;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.storemanager.StorePool;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
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
    
    @PostConstruct
    public void createSmsFactory() {
        smsFactory = new SMSFactoryImpl(logger, database, frameworkConfig, databaseSaver, storeManager, storeApplicationPool);
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
        smsFactory.send("", to, message);
    }
}
