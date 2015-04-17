/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.Logger;
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
    private MailConfig configuration;

    @Autowired
    public FrameworkConfig frameworkConfig;

    @Autowired
    public Logger logger;
    private Map<String, String> files;
    private boolean delete;

    public void setMailConfiguration(MailConfig configuration) {
        this.configuration = configuration;
    }

    private Session getSession() {
        Authenticator authenticator = new Authenticator();

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
        properties.setProperty("mail.smtp.auth", "true");

        properties.setProperty("mail.smtp.host", configuration.getSettings().hostname);
        properties.setProperty("mail.smtp.port", "" + configuration.getSettings().port);

        if (configuration.getSettings().enableTls) {
            properties.setProperty("mail.smtp.starttls.enable", "true");
        }

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
        mfi.frameworkConfig = frameworkConfig;

        if (to == null || to.equals("test@getshop.com")) {
            //Send this to noone, or the test account.. no way!
            return;
        }
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
        mfi.configuration = configuration;
        mfi.logger = logger;
        mfi.storeId = storeId;
        mfi.frameworkConfig = frameworkConfig;

        if (to == null || to.equals("test@getshop.com")) {
            //Send this to noone, or the test account.. no way!
            return;
        }
        new Thread(mfi).start();
    }

    private void warnNotDelivered() {
        System.out.println("WARNING:!!!!!!!!!!!!!!!! MAIL NOT DELIVERED: " + subject + " from: " + from +  " to  " + to);
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
        boolean delivered = false;
        for (int i = 0; i < 10; i++) {
            try {
                message.setSubject(subject, "UTF-8");
                message.setHeader("Content-Type", "text/plain; charset=UTF-8");
                message.addRecipient(RecipientType.TO, new InternetAddress(to));
                message.addFrom(new InternetAddress[]{new InternetAddress(configuration.getSettings().sendMailFrom)});
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
                    delivered = true;
                    break;
                } else {
                    System.out.println("Mail sent to: " + to + ", from: " + from + ", subject: " + subject + ", content: " + content);
                    break;
                }
            } catch (Exception ex) {
                logger.error(this, "Was not able to send email to" + to + " from: " + from + " subject: " + subject);
                ex.printStackTrace();
            }
            try {
                Thread.sleep(1000*60*30);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        if(!delivered) {
            warnNotDelivered();
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
