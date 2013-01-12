/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.Logger;
import com.thundashop.core.common.StoreComponent;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MailFactoryImpl extends StoreComponent implements MailFactory, Runnable {
    private String from;
    private String to;
    private String subject;
    private String content;
    private String storeId;
    
    @Autowired
    private MailConfiguration configuration;
    
    @Autowired
    public Logger logger;

    private Session getSession() {
        Authenticator authenticator = new Authenticator();

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
        properties.setProperty("mail.smtp.auth", "true");

        properties.setProperty("mail.smtp.host", configuration.getSettings().hostname);
        properties.setProperty("mail.smtp.port", "" + configuration.getSettings().port);
        
        if (configuration.getSettings().enableTls)
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
        mfi.configuration = configuration;
        mfi.logger = logger;
        mfi.storeId = storeId;
        if(to == null || to.equals("test@getshop.com")) {
            //Send this to noone, or the test account.. no way!
            return;
        }
        new Thread(mfi).start();
    }

    @Override
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    private class Authenticator extends javax.mail.Authenticator {

        private PasswordAuthentication authentication;

        public Authenticator() {
            authentication = new PasswordAuthentication(configuration.getSettings().username, configuration.getSettings().password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }

    @Override
    public void run() {
        configuration.setup(storeId);
        MimeMessage message = new MimeMessage(getSession());
        
        try {
            message.addRecipient(RecipientType.TO, new InternetAddress(to));
            message.addFrom(new InternetAddress[]{new InternetAddress(configuration.getSettings().sendMailFrom)});
            message.setReplyTo(new InternetAddress[]{new InternetAddress(from)});

            message.setSubject(subject, "UTF-8");
            message.setHeader("Content-Type", "text/plain; charset=UTF-8");
            message.setContent(content, "text/html; charset=UTF-8");

            Transport.send(message);
        } catch (Exception ex) {
            logger.error(this, "Was not able to send email... ", ex);
        }
    }
}