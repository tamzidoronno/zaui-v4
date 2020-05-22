/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.GrafanaFeederImpl;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.ordermanager.data.Order;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        HashMap<String, String> res = validatePhone("+"+prefix, to, "NO", true);
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
        encodeFrom();
        this.productionMode = productionMode;
        this.message = message;
        createSmsMessage();
    }

    private void encodeFrom() {
        if (this.from != null) {
            try {
                this.from = java.net.URLEncoder.encode(this.from, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SmsHandlerAbstract.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    public static HashMap<String, String> validatePhone(String phonePrefix, String phone, String countryCode, boolean verifyPrefixOnCountryCode) {
        if(phone == null) {
            return null;
        }
        phone = phone.replaceAll("\\.", "");
        phone = phone.replaceAll(" ", "");
        
        phonePrefix = phonePrefix.replace("++", "+");
        phonePrefix = phonePrefix.replace("++", "+");
        phonePrefix = phonePrefix.replace("++", "+");
        phonePrefix = phonePrefix.replace("++", "+");
        
        if(!phonePrefix.startsWith("+")) {
            phonePrefix = "+" + phonePrefix;
        }
        
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        if(countryCode == null) {
            countryCode = "+47";
        } else {
            try {
                if(countryCode != null && !countryCode.isEmpty()) {
                    String defaultCountryCodeSetByStore = "no";
                    if(!countryCode.equalsIgnoreCase(defaultCountryCodeSetByStore) && verifyPrefixOnCountryCode) {
                        //If the country code is a different one then the default set for the store, 
                        //That countrycode should have priority.
                        int fromutil = phoneUtil.getCountryCodeForRegion(countryCode);
                        if(fromutil > 0) {
                            if(!("+" + fromutil).equals(phonePrefix)) {
                                phonePrefix = "+" + fromutil;
                            }
                        }
                    }
                }
            }catch(Exception g) {
                //If it fail, ignore it and continue.
            }
        }
        
        String phoneNumberToCheck = phonePrefix+phone;
        if(phoneNumberToCheck.indexOf("+", 1) > 0) {
            phoneNumberToCheck = phoneNumberToCheck.substring(phoneNumberToCheck.indexOf("+", 1));
        }
        
        
        String prefix = "";
        phone = phone.replace("++", "+");
        phone = phone.replace("++", "+");
        phone = phone.replace("++", "+");
        phone = phone.replace("++", "+");
        try {
            Phonenumber.PhoneNumber phonecheck = phoneUtil.parse(phoneNumberToCheck, countryCode);
            if (!phoneUtil.isValidNumber(phonecheck)) {
                String phone2 = phone;
                if (phone.startsWith("0000")) {
                    phone2 = phone.substring(4);
                } else if (phone.startsWith("000")) {
                    phone2 = phone.substring(3);
                } else if (phone.startsWith("00")) {
                    phone2 = phone.substring(2);
                }

                phonecheck = phoneUtil.parse(phonePrefix+phone2, countryCode);
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
            HashMap<String, String> result = new HashMap();
            if(prefix.isEmpty()) {
                prefix = phonePrefix.replace("+", "");
                prefix = prefix.replace("+", "");
                prefix = prefix.replace("+", "");
                prefix = prefix.replace("+", "");
            }
            result.put("prefix", prefix);
            result.put("phone", phone);
            return result;
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
        if (message != null) {
            message = message.replace("<br/>", "\n");
            message = message.replace("<br>", "\n");
        }
        
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public static String getCountryCodeOfPhonePrefix(String prefix) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String code = phoneUtil.getRegionCodeForCountryCode(new Integer(prefix));
        return code;
    }
    
    private void createSmsMessage() {
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.from = from;
        smsMessage.to = to;
        smsMessage.prefix = prefix;
        smsMessage.message = message;
        smsMessage.status = "not_delivered";
        smsMessage.smsHander = getName();
        smsMessage.rowCreatedDate = new Date();
        
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
            Thread td = new Thread((Runnable) this);
            td.setName("Send sms thread for store: " + storeId);
            td.start();
        } else {
            GetShopLogHandler.logPrintStatic("SMS Sent with " + getName() + " [to:  " + to + ", from: " +from + ", prefix: " + prefix + ", msg: " + message +" ]", null);
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