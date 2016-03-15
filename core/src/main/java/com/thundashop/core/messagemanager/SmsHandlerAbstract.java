/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.thundashop.core.databasemanager.Database;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author ktonder
 */
public abstract class SmsHandlerAbstract implements Runnable {

    private String storeId;
    private Database database;
    private String prefix;
    private String from;
    private String to;
    private String message;
    private SmsMessage smsMessage;
    private boolean productionMode = false;
    
    public SmsHandlerAbstract(String storeId, Database database, String prefix, String from, String to, String message, boolean productionMode) {
        HashMap<String, String> res = validatePhone(to, prefix);
        if(res != null) {
            this.prefix = res.get("prefix");
            this.to = res.get("phone");
        } else {
            this.prefix = prefix;
            this.to = to;
        }
        
        this.storeId = storeId;
        this.database = database;
        this.from = from;
        this.productionMode = productionMode;
        this.message = message;
        createSmsMessage();
    }

    
    private HashMap<String, String> validatePhone(String phone, String countryCode) {
        if(phone == null) {
            return null;
        }
        
        if(countryCode == null) {
            countryCode = "+47";
        }
        
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String prefix = "";
        phone = phone.replace("++", "+");
        phone = phone.replace("++", "+");
        phone = phone.replace("++", "+");
        phone = phone.replace("++", "+");
        try {
            Phonenumber.PhoneNumber phonecheck = phoneUtil.parse(phone, countryCode);
            if (!phoneUtil.isValidNumber(phonecheck)) {
                String phone2 = phone;
                if (phone.startsWith("0000")) {
                    phone2 = phone.substring(4);
                } else if (phone.startsWith("000")) {
                    phone2 = phone.substring(3);
                } else if (phone.startsWith("00")) {
                    phone2 = phone.substring(2);
                }

                phonecheck = phoneUtil.parse(phone2, countryCode);
                prefix = phonecheck.getCountryCode() + "";
                phone = phonecheck.getNationalNumber() + "";

                if (!phoneUtil.isValidNumber(phonecheck)) {
                    phone2 = "00" + phone;
                    phonecheck = phoneUtil.parse(phone2, countryCode);

                    if (!phoneUtil.isValidNumber(phonecheck)) {
                        if (phone.length() == 10 && phone.startsWith("07")) {
                            phone = phone.substring(1);
                            prefix = "46";
                        } else if (phone.length() == 9 && phone.startsWith("7")) {
                            prefix = "46";
                        } else {
                            return null;
                        }
                    } else {
                        prefix = phonecheck.getCountryCode() + "";
                        phone = phonecheck.getNationalNumber() + "";
                    }
                } else {
                    prefix = phonecheck.getCountryCode() + "";
                    phone = phonecheck.getNationalNumber() + "";
                }
            } else {
                prefix = phonecheck.getCountryCode() + "";
                phone = phonecheck.getNationalNumber() + "";
            }
        } catch (NumberParseException e) {
            return null;
        }

        HashMap<String, String> result = new HashMap();
        result.put("prefix", prefix);
        result.put("phone", phone);
        return result;
    }
    
    public abstract String getName();

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFrom() {
        if (from == null || from.isEmpty()) {
            return "GetShop";
        }
        
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        if (to != null) {
            to = to.replace("+", "");
        }
        
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    private void createSmsMessage() {
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.from = from;
        smsMessage.to = to;
        smsMessage.prefix = prefix;
        smsMessage.message = message;
        smsMessage.status = "not_delivered";
        smsMessage.smsHander = getName();
        
        database.save(MessageManager.class.getSimpleName(), getColName(), smsMessage);
        this.smsMessage = smsMessage;
    }

    private String getColName() {
        return "col_"+storeId+"_log";
    }
    
    public String getMessageId() {
        return this.smsMessage.id;
    }
    
    public void sendMessage() {
        if (productionMode) {
            new Thread((Runnable) this).run();
        } else {
            System.out.println("SMS Sent with " + getName() + " [to:  " + to + ", from: " +from + ", prefix: " + prefix + ", msg: " + message +" ]");
        }
    }
    
    public abstract void postSms() throws Exception;
    
    @Override
    public void run() {
        try {
            postSms();
        } catch (Exception ex) {
            ex.printStackTrace();
            logMessage("failed", ex.getMessage());
        }
    }
    
    protected void logMessage(String status, String response) {
        smsMessage.status = status;
        smsMessage.response = response;
        database.save(MessageManager.class.getSimpleName(), getColName(), smsMessage);
    }
    
    protected DefaultHttpClient wrapClient(DefaultHttpClient base) {
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
}