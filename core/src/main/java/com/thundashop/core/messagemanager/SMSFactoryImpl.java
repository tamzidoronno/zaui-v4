/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
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
        storeId = storeApplicationPool.getStoreId();
    }
    
    public static DefaultHttpClient wrapClient(DefaultHttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
     public String postSms(String src, String dst, String text, String msgId) {

        DefaultHttpClient client = new DefaultHttpClient();
        client = wrapClient(client);
        HttpResponse httpResponse = null;
        

        HttpEntity entity;
        byte[] bytes = ("MAMGUYNGZMMWMWOWM2MD:ODJjZmQyYjExZDZhMjdmMTg4YWI2MjJiZWQ3NDky").getBytes();
        String encoding = Base64.encode(bytes);

        try {
            if(msgId == null) {
                dst = dst.replace("+","");
                HttpPost request = new HttpPost("https://api.plivo.com/v1/Account/MAMGUYNGZMMWMWOWM2MD/Message/");
                request.addHeader("Authorization", "Basic " + encoding);

                StringBody srcbody = new StringBody(src, ContentType.TEXT_PLAIN);
                StringBody dstbody = new StringBody(dst, ContentType.TEXT_PLAIN);
                StringBody textbody = new StringBody(text, ContentType.TEXT_PLAIN);

                HttpEntity reqEntity = MultipartEntityBuilder.create()
                        .addPart("src", srcbody)
                        .addPart("dst", dstbody)
                        .addPart("text", textbody)
                        .build();

                request.setEntity(reqEntity);
                httpResponse = client.execute(request);
            } else {
                HttpGet request = new HttpGet("https://api.plivo.com/v1/Account/MAMGUYNGZMMWMWOWM2MD/Message/" + msgId);
                request.addHeader("Authorization", "Basic " + encoding);
                httpResponse = client.execute(request);
           }

            System.out.println("Now sending sms, " + src + " | " + dst + " | " + text + " | "  + msgId);
            entity = httpResponse.getEntity();
            System.out.println("Done sending sms");

            if (entity != null) {
                InputStream instream = entity.getContent();
                int ch;
                StringBuilder sb = new StringBuilder();
                while ((ch = instream.read()) != -1) {
                    sb.append((char) ch);
                }
                String result = sb.toString();
                return result.trim();
            }
        } catch (ClientProtocolException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        return "failed";
    }
    
    
    @Override
    public void send(String from, String to, String message) {
        Application smsSettingsApp = storeApplicationPool.getApplication("12fecb30-4e5c-49d8-aa3b-73f37f0712ee");
        
        if (smsSettingsApp == null) {
            smsSettingsApp = new Application();
        }
        
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
        impl.username = smsSettingsApp.getSetting("username");
        impl.apiId = smsSettingsApp.getSetting("apiid");
        impl.password = smsSettingsApp.getSetting("password");
        if(prefix != null) {
            impl.prefix = prefix;
        } else {
            impl.prefix = smsSettingsApp.getSetting("numberprefix");
        }
        if(from != null && !from.isEmpty()) {
            impl.from = from;
        } else {
            impl.from = smsSettingsApp.getSetting("from");
        }
        impl.messageManager = messageManager;
        
        
        new Thread(impl).start();
    }
    
    public void run() {
        
        if (!frameworkConfig.productionMode) {
            System.out.println("Sent SMS [ to: (" +prefix + ") " + to + ", from: " + from +", Message: " + message + " ]");
            return;
        }
        
        if (to == null) {
            return;
        }
        
        to = to.replace("+", "");
        
        if(from == null || from.isEmpty()) {
            from = "GetShop";
        }

        Gson gson = new Gson();
        String response = postSms(from, prefix+to, message, null);
        PlivoResponse plivoresponse = gson.fromJson(response, PlivoResponse.class);

        if(plivoresponse.message_uuid.size() > 0) {
            SmsLogEntry entry = new SmsLogEntry();
            entry.responseCode = "0";
            entry.message = message;
            entry.to = to;
            entry.apiId = apiId;
            entry.prefix = prefix;
            entry.from = from;
            entry.msgId = plivoresponse.message_uuid.get(0);

            this.messageManager.saveToLog(entry);
        } else {
            logger.error(this, "Could not send sms to " + to + " from " + from + " message: " + message);
            System.out.println(response);
            return;
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

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getMessageState(String msgId) {
        String result = postSms(null, null, null, msgId);
        Gson gson = new Gson();
        PlivoMessageState state = gson.fromJson(result, PlivoMessageState.class);
        return state.message_state;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }
}