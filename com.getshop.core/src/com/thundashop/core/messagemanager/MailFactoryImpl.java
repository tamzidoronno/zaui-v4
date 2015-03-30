/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.Setting;
import com.thundashop.core.common.StoreComponent;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@GetShopSession
public class MailFactoryImpl extends StoreComponent implements MailFactory, Runnable {
    private String from;
    private String to;
    private String subject;
    private String content;
    private String storeId;
    
    private MailSettings mailSettings;
    
    @Autowired
    public FrameworkConfig frameworkConfig;

    @Autowired
    private StoreApplicationPool storeApplicationPool;
    
    @Autowired
    public Logger logger;
    private Map<String, String> files;
    private boolean delete;
    
    private MailSettings getMailSettings() {
        Application mailApplication = storeApplicationPool.getApplicationWithSecuredSettings("8ad8243c-b9c1-48d4-96d5-7382fa2e24cd");
        Map<String, Setting> confSettings = null;
        
        if (mailApplication != null) {
            confSettings = mailApplication.settings;
        }
        
        MailSettings settings = new MailSettings();
        if (confSettings != null) {
            if (confSettings.get("hostname") != null) {
                settings.hostname = confSettings.get("hostname").value;
            }
            
            if (confSettings.get("port") != null) {
                settings.port = Integer.valueOf(confSettings.get("port").value);
            }
            
            if (confSettings.get("password") != null) {
                settings.password = confSettings.get("password").value;
            }
            
            if (confSettings.get("username") != null) {
                settings.username = confSettings.get("username").value;
                settings.sendMailFrom = confSettings.get("username").value;
            }
            
            if (confSettings.get("enabletls") != null) {
                String enableTls = confSettings.get("enabletls").value;
                if (enableTls != null && enableTls.equals("true")) {
                    settings.enableTls = true;
                }
            }
        }
        
        return settings;
    }
 
    private Session getSession() {
        Authenticator authenticator = new Authenticator();

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
        properties.setProperty("mail.smtp.auth", "true");

        properties.setProperty("mail.smtp.host", mailSettings.hostname);
        properties.setProperty("mail.smtp.port", "" + mailSettings.port);
        
        if (mailSettings.enableTls)
            properties.setProperty("mail.smtp.starttls.enable", "true");
        
        return Session.getInstance(properties, authenticator);
    }

    @Override
    public void send(String from, String to, String title, String content) {
        MailFactoryImpl mfi = new MailFactoryImpl();
        mfi.from = from;
        mfi.to = to;
        mfi.subject = title;
        mfi.content = content;
        mfi.mailSettings = getMailSettings();
        mfi.logger = logger;
        mfi.storeId = storeId;
        mfi.frameworkConfig = frameworkConfig;
        
        new Thread(mfi).start();
    }

    @Override
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    /**
     * 
     * @param from
     * @param to
     * @param title
     * @param content
     * @param files Filepath, filename.
     * @param delete 
     */
    @Override
    public void sendWithAttachments(String from, String to, String title, String content, Map<String, String> files, boolean delete) {
        MailFactoryImpl mfi = new MailFactoryImpl();
        mfi.from = from;
        mfi.to = to;
        mfi.files = files;
        mfi.delete = delete;
        mfi.subject = title;
        mfi.content = content;
        mfi.mailSettings = getMailSettings();
        mfi.logger = logger;
        mfi.storeId = storeId;
        mfi.frameworkConfig = frameworkConfig;
        
        if(to == null || to.equals("test@getshop.com")) {
            //Send this to noone, or the test account.. no way!
            return;
        }
        new Thread(mfi).start();
    }

    private class Authenticator extends javax.mail.Authenticator {

        private PasswordAuthentication authentication;

        public Authenticator() {
            if (mailSettings == null) {
                mailSettings = new MailSettings();
            }
            authentication = new PasswordAuthentication(mailSettings.username, mailSettings.password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }

    @Override
    public void run() {
        MimeMessage message = new MimeMessage(getSession());
        boolean delivered = false;
        for(int i = 0; i < 24; i++) {
            try {
                message.setSubject(subject, "UTF-8");
                message.setHeader("Content-Type", "text/plain; charset=UTF-8");
                message.addRecipient(RecipientType.TO, new InternetAddress(to));
                message.addFrom(new InternetAddress[]{new InternetAddress(mailSettings.sendMailFrom)});
                message.setReplyTo(new InternetAddress[]{new InternetAddress(from)});

                if (files != null) {
                    Multipart multipart = new MimeMultipart("mixed");

                    BodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setContent(content, "text/html; charset=UTF-8");
                    multipart.addBodyPart(messageBodyPart);

                    for (String file : files.keySet()) {
                        DataSource source = new FileDataSource(file);
                        messageBodyPart = new MimeBodyPart();
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(files.get(file));
                        multipart.addBodyPart(messageBodyPart);
                    }

                    message.setContent(multipart);
                } else {
                    message.setContent(content, "text/html; charset=UTF-8");
                }

                if (frameworkConfig.productionMode) {
                    Transport.send(message);
                } else {
                    System.out.println("Mail sent to: " + to + ", from: "+from+", subject: " + subject + ", content: " + content);
                }
                delivered = true;
                break;
            } catch (Exception ex) {
                logger.error(this, "Was not able to send email on try: " + i + "( message: " + from + " - " + to + " " + subject + content + "");
                ex.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        if(!delivered) {
            logger.error(this, "Giving up sending email to -> message: " + from + " - " + to + " " + subject + content + "");
            try {
                message.setSubject("Failed on: " + subject);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        if (delete && !files.isEmpty()) {
            for (String file : files.keySet()) {
                try {
                    File fileToDelete = new File(file);
                    fileToDelete.delete();
                    File fileToDelete2 = new File(files.get(file));
                    fileToDelete2.delete();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}