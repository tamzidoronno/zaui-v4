/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.services.config.FrameworkConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Properties;


@Component
@GetShopSession
public class MailFactoryImpl extends StoreComponent implements MailFactory, Runnable {
    private String from;
    private String replyTo;
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
    private Database database; 
    
    @Autowired
    GrafanaManager grafanaManager;
    
    @Autowired
    public Logger logger;

    @Autowired
    @Qualifier("mailSenderExecutor")
    private TaskExecutor mailSenderExecutor;

    private Map<String, String> files;
    private boolean delete;
    private MailMessage logMessage;
    
    private MailSettings getMailSettings() {
        Application mailApplication = storeApplicationPool.getApplicationWithSecuredSettings("8ad8243c-b9c1-48d4-96d5-7382fa2e24cd");
        Map<String, Setting> confSettings = null;
        
        if (mailApplication != null) {
            confSettings = mailApplication.settings;
        }
        
        MailSettings settings = new MailSettings();
        if (confSettings != null) {
            if (confSettings.get("hostname") != null && confSettings.get("hostname").value != null && !confSettings.get("hostname").value.isEmpty()) {
                settings.hostname = confSettings.get("hostname").value;
            }
            
            if (confSettings.get("port") != null && confSettings.get("port").value != null && !confSettings.get("port").value.isEmpty()) {
                settings.port = Integer.valueOf(confSettings.get("port").value);
            }
            
            if (confSettings.get("password") != null && confSettings.get("password").value != null && !confSettings.get("password").value.isEmpty()) {
                settings.password = confSettings.get("password").value;
            }
            if (confSettings.get("sendMailFromName") != null && confSettings.get("sendMailFromName").value != null && !confSettings.get("sendMailFromName").value.isEmpty()) {
                settings.fromName = confSettings.get("sendMailFromName").value;
            }
            
            if (confSettings.get("replyTo") != null && confSettings.get("replyTo").value != null && !confSettings.get("replyTo").value.isEmpty()) {
                settings.replyTo = confSettings.get("replyTo").value;
            }
            
            if (confSettings.get("username") != null && confSettings.get("username").value != null && !confSettings.get("username").value.isEmpty()) {
                settings.username = confSettings.get("username").value;
                settings.sendMailFrom = confSettings.get("username").value;
            }
            
            if (confSettings.get("sendMailFrom") != null && confSettings.get("sendMailFrom").value != null && !confSettings.get("sendMailFrom").value.isEmpty()) {
                String sendfrom = confSettings.get("sendMailFrom").value;
                if(sendfrom != null && !sendfrom.isEmpty()) {
                    settings.sendMailFrom = sendfrom;
                }
            }

            if (confSettings.get("enabletls") != null && confSettings.get("enabletls").value != null && !confSettings.get("enabletls").value.isEmpty()) {
                String enableTls = confSettings.get("enabletls").value;
                if (enableTls != null && enableTls.equals("true")) {
                    settings.enableTls = true;
                } else if ("false".equals(enableTls)) {
                    settings.enableTls = false;
                }
            }
        }
        
        return settings;
    }
 
    private Session getMailSession() {
        Authenticator authenticator = new Authenticator();

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.host", mailSettings.hostname);
        properties.setProperty("mail.smtp.port", "" + mailSettings.port);
        if (mailSettings.enableTls) {
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        }
        
        return Session.getInstance(properties, authenticator);
    }

    @Override
    public String send(String from, String to, String title, String content) {
        MailMessage message = createMailMessage(to, from, title, content);
        if (to != null && !to.isEmpty()) {
            if (to.contains(";")) {
                for (String email : to.split(";")) {
                    message = createMailMessage(email, from, title, content);
                    String useFrom = from.contains(";") ? email : from;
                    MailFactoryImpl mfi = createMailFactory(useFrom, email, title, content, message);
                    mailSenderExecutor.execute(mfi);
                }
            } else {
                MailFactoryImpl mfi = createMailFactory(from, to, title, content, message);
                mailSenderExecutor.execute(mfi);
            }
        }
        
        if (message == null) {
            return "";
        }
        
        return message.id;
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
    public String sendWithAttachments(String from, String to, String title, String content, Map<String, String> files, boolean delete) {
        MailMessage mailMessage = createMailMessage(to, from, title, content);
        mailMessage.hasAttachments = true;
        
        if (to != null && !to.isEmpty()) {
            if (to.contains(";")) {
                for (String email : to.split(";")) {
                    MailFactoryImpl mfi = createMailFactory(from, email, title, content, mailMessage);
                    mfi.files = files;
                    mfi.delete = delete;
                    mailSenderExecutor.execute(mfi);
                }
            } else {
                MailFactoryImpl mfi = createMailFactory(from, to, title, content, mailMessage);
                mfi.files = files;
                mfi.delete = delete;
                mailSenderExecutor.execute(mfi);
            }
        }
        
        if (mailMessage == null) {
            return "";
        }
        
        return mailMessage.id;
    }

    private MailFactoryImpl createMailFactory(String from, String to, String title, String content, MailMessage message) {
        MailFactoryImpl mfi = new MailFactoryImpl();
        mfi.from = from;
        mfi.to = to;
        mfi.subject = title;
        mfi.content = content;
        mfi.mailSettings = getMailSettings();
        mfi.logger = logger;
        mfi.storeId = storeId;
        mfi.frameworkConfig = frameworkConfig;
        mfi.logMessage = message;
        mfi.database = database;
        return mfi;
    }

    private MailMessage createMailMessage(String to, String from, String title, String content) {
        if (storeId == null || storeId.isEmpty()) {
            return null;
        }
        
        MailMessage mailMessage = new MailMessage();
        mailMessage.to = to;
        mailMessage.subject = title;
        mailMessage.content = content;
        mailMessage.storeId = storeId;
        
        database.save(MessageManager.class.getSimpleName(), getColName(), mailMessage);
        return mailMessage;
    }
    
    private void updateMailStatus(String status) {
        logMessage.status = status;
        database.save(MessageManager.class.getSimpleName(), getColName(), logMessage);
    }

    private String getColName() {
        return "col_"+storeId+"_log";
    }

    private boolean isToDeveloper(String to) {
        return to.contains("@cefalo.com");
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
        MimeMessage message = new MimeMessage(getMailSession());
        boolean delivered = false;
        if(from == null || !from.contains("@")) {
            from = "post@getshop.com";
        }
        
        if (mailSettings.sendMailFrom != null && mailSettings.sendMailFrom.contains("@") && !mailSettings.sendMailFrom.equals("noreply@getshop.com")) {
            from = mailSettings.sendMailFrom;
        }
        if (mailSettings.replyTo != null && mailSettings.replyTo.contains("@")) {
            replyTo = mailSettings.replyTo;
        }
        if(replyTo == null || replyTo.isEmpty()) {
            replyTo = from;
        }
        

        if(to == null || !to.contains("@")) {
            GetShopLogHandler.logPrintStatic("Unable to send email to : " + to + " since it does not contain an @", storeId);
            return;
        }
        boolean authError = false;
        for(int i = 0; i < 10; i++) {
            try {
                message.setSubject(subject, "UTF-8");
                message.setHeader("Content-Type", "text/plain; charset=UTF-8");
                message.addRecipient(RecipientType.TO, new InternetAddress(to));
                if(mailSettings.fromName != null && !mailSettings.fromName.isEmpty()) {
                    message.addFrom(new InternetAddress[]{new InternetAddress(mailSettings.sendMailFrom, mailSettings.fromName)});
                } else {
                    message.addFrom(new InternetAddress[]{new InternetAddress(mailSettings.sendMailFrom)});
                }
                message.setReplyTo(new InternetAddress[]{new InternetAddress(replyTo)});
                message.setSentDate(new Date());
                
                
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

                if (frameworkConfig.productionMode || isToDeveloper(to)) {
                    Transport.send(message);
                } else {
                    GetShopLogHandler.logPrintStatic("Mail sent to: " + to + ", from: "+from+", subject: " + subject + ", content: " + content.replace("<br>", "\n"), storeId);
                }
                updateMailStatus("delivered");
                delivered = true;
                break;
            } catch (Exception ex) {
                if(ex instanceof AuthenticationFailedException) {
                    GetShopLogHandler.logPrintStatic("Authentication error on email", storeId);
                    authError = true;
                    break;
                }
                updateMailStatus("failed");
                if(authError) {
                    GetShopLogHandler.authenticationError.add(storeId);
                }
                GetShopLogHandler.logPrintStatic("Was not able to send email on try: " + i + "( message: " + from + " - " + to + " " + subject + content + "", storeId);
                GetShopLogHandler.logStack(ex, storeId);
            }
            try {
                Thread.sleep(10000);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        if(!delivered) {
            GetShopLogHandler.logPrintStatic("Giving up sending email to -> message: " + from + " - " + to + " " + subject + content + "", storeId);
            try {
                message.setSubject("Failed on: " + subject);
            }catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            GetShopLogHandler.authenticationError.remove(storeId);
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