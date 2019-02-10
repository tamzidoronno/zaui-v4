/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.googleapi;

import com.getshop.scope.GetShopSession;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.sun.mail.util.BASE64DecoderStream;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GmailApiManager extends ManagerBase implements IGmailApiManager {

    private static final String APPLICATION_NAME = "GetShop";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private HashMap<String, GmailMessageLight> gmailMessages = new HashMap();
    private Gmail service = null;

    @Autowired
    private UserManager userManager;

    @Override
    public void fetchAllMessages() {
        try {
            doFolder("in:inbox", false);
//            doFolder("in:inbox/behandlet", false);
//            doFolder("in:sent", true);
        } catch (IOException ex) {
            Logger.getLogger(GmailApiManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon d : data.data) {
            if (d instanceof GmailMessageLight) {
                gmailMessages.put(d.id, (GmailMessageLight) d);
            }
        }
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, File credentialFile) throws IOException {
        InputStream in = new FileInputStream(credentialFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        List<String> scopesToUse = new ArrayList();
        scopesToUse.add(GmailScopes.GMAIL_LABELS);
        scopesToUse.add(GmailScopes.GMAIL_READONLY);
        scopesToUse.add(GmailScopes.GMAIL_SEND);
        scopesToUse.add(GmailScopes.GMAIL_COMPOSE);

        String tokenPath = "tokens/googleapi/" + storeId;
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopesToUse)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokenPath)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(18888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void doFolder(String q, boolean sentFolder) throws IOException {
        Gmail.Users.Messages messages = getService().users().messages();
        Gmail.Users.Messages.List request = messages.list("me");

        // INBOX
        long time = System.currentTimeMillis();
        request.setQ(q);

        ListMessagesResponse response = request.execute();

        while (true) {
            boolean lastPage = response.getNextPageToken() == null || response.getNextPageToken().isEmpty();
            List<Message> allMessages = response.getMessages();

            if (response.getResultSizeEstimate() == 0) {
                break;
            }

            for (Message msg : allMessages) {

                if (!gmailMessages.containsKey(msg.getId())) {
                    Message message = service.users().messages().get("me", msg.getId()).setFormat("metadata").execute();
                    Message messageRaw = service.users().messages().get("me", msg.getId()).setFormat("raw").execute();
                    GmailMessage getshopMsg = new GmailMessage();

                    getshopMsg.rawMessage = messageRaw.getRaw();
                    saveObject(getshopMsg);

                    createLightObject(getshopMsg, message, sentFolder);
                }
            }

            request.setPageToken(response.getNextPageToken());
            response = request.execute();

            if (response == null || lastPage) {
                break;
            }

        }
    }

    public void createConnection() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        File file = new File("google-api-credentials/gmailcredentials.json");

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential cred = getCredentials(HTTP_TRANSPORT, file);

        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Gmail getService() {
        if (service == null) {
            try {
                createConnection();
            } catch (IOException ex) {
                Logger.getLogger(GmailApiManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (GeneralSecurityException ex) {
                Logger.getLogger(GmailApiManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (service == null) {
            throw new ErrorException(1059);
        }

        return service;
    }

    private void createLightObject(GmailMessage getshopMsg, Message msg, boolean inSent) {
        GmailMessageLight light = new GmailMessageLight();
        light.id = msg.getId();
        light.inSent = inSent;

        light.messageId = getshopMsg.id;
        light.historyId = msg.getHistoryId().longValue();
        light.snippet = msg.getSnippet();
        light.date = new Date(msg.getInternalDate());
        light.threadId = msg.getThreadId();

        List headers = (List) msg.getPayload().get("headers");

        for (Object i : headers) {
            MessagePartHeader header = (MessagePartHeader) i;

            if (header.getName().equals("Subject")) {
                light.subject = header.getValue();
            }

            if (header.getName().equals("Delivered-To")) {
                light.deliveredTo = header.getValue();
            }

            if (header.getName().equals("From")) {
                light.from = header.getValue();
            }

            if (header.getName().equals("Content-Type")) {
                light.contentType = header.getValue();
            }

            if (header.getName().equals("To")) {
                light.to = header.getValue();
            }

            if (header.getName().equals("Content-Transfer-Encoding")) {
                light.transferEncoding = header.getValue();
            }

            if (header.getName().equals("Return-Path")) {
                light.replyTo = header.getValue();
            }
        }

        saveObject(light);
        gmailMessages.put(light.id, light);

        checkForCompanyConnection(light);
    }

    @Override
    public List<GmailMessageLight> getLightMessages(String companyId) {

        List<User> companyUsers = userManager.getUsersByCompanyId(companyId);

        List<GmailMessageLight> retList = gmailMessages.values()
                .stream()
                .filter(o -> isToCompany(o, companyUsers))
                .collect(Collectors.toList());

        sortAndFinalize(retList);

        return retList;
    }

    private void sortAndFinalize(List<GmailMessageLight> retList) {
        retList.sort((GmailMessageLight o1, GmailMessageLight o2) -> {
            return o2.date.compareTo(o1.date);
        });

        retList.stream()
                .filter(o -> o.connectedToCompanyId != null && !o.connectedToCompanyId.isEmpty())
                .forEach(o -> {
                    finalizeMessage(o);
                });
    }

    private void finalizeMessage(GmailMessageLight o) {
        if (o == null) {
            return;
        }

        Company comp = userManager.getCompany(o.connectedToCompanyId);
        if (comp != null) {
            o.connectedToCompanyName = comp.name;
        }
    }

    private boolean isToCompany(GmailMessageLight o, List<User> companyUsers) {
        for (User user : companyUsers) {
            String emailAddress = user.emailAddress;

            if (emailAddress.isEmpty()) {
                continue;
            }

            if (o.to.toLowerCase().contains(emailAddress) || o.from.toLowerCase().contains(emailAddress)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<GmailMessageLight> getAllUnassignedMessages() {
        List<GmailMessageLight> msgs = gmailMessages.values()
                .stream()
                .filter(o -> o.isUnassigned() && o.needsProcessing())
                .collect(Collectors.toList());

        sortAndFinalize(msgs);
        return msgs;
    }

    @Override
    public List<GmailMessageLight> getMyUnsolvedMessages() {
        List<GmailMessageLight> retList = gmailMessages.values()
                .stream()
                .filter(o -> o.isAssignedTo(getSession().currentUser.id) && o.needsProcessing())
                .collect(Collectors.toList());

        sortAndFinalize(retList);
        return retList;
    }

    @Override
    public void assignMessageToUser(String messageId, String userId) {
        GmailMessageLight msg = gmailMessages.get(messageId);
        if (msg != null) {
            msg.assignTo(getSession().currentUser.id, userId);
            saveObject(msg);
        }
    }

    public void reScanCompanyConnection() {
        gmailMessages.values().stream()
                .filter(o -> o.connectedToCompanyName == null || o.connectedToCompanyName.isEmpty())
                .forEach(o -> {
                    checkForCompanyConnection(o);
                });
    }

    private void checkForCompanyConnection(GmailMessageLight o) {
        Company company = userManager.getCompanyByEmail(o.getFromEmailAddress());
        if (company != null) {
            o.connectedToCompanyId = company.id;
            saveObject(o);
        }
    }

    @Override
    public void connectMessageToCompany(String msgId, String companyId) {
        GmailMessageLight msg = gmailMessages.get(msgId);

        User user = new User();
        user.fullName = msg.getName();
        user.emailAddress = msg.getFromEmailAddress();
        user.company.add(companyId);
        userManager.saveUser(user);

        reScanCompanyConnection();
    }

    @Override
    public void markAsArchived(String messageId) {
        GmailMessageLight msg = gmailMessages.get(messageId);
        if (msg != null) {
            msg.markAsArchived(getSession().currentUser.id);
            saveObject(msg);
        }
    }

    @Override
    public List<GmailMessagePart> getMessageParts(String id) {
        DataCommon com = database.findObject(id, "GmailApiManager");
        List<GmailMessagePart> retList = new ArrayList();

        if (com != null) {
            try {
                MimeMessage message = toMimeMessage(((GmailMessage) com).rawMessage);
                String contentType = message.getContentType();

                if (contentType.contains("multipart")) {
                    Multipart multiPart = (Multipart) message.getContent();
                    addMultiPart(multiPart, retList);
                } else if (contentType.contains("text/plain")) {
                    GmailMessagePart gmailMessagePart = new GmailMessagePart();
                    gmailMessagePart.text = (String) message.getContent();
                    retList.add(gmailMessagePart);
                } else {
                    System.out.println("Part not handled: " + contentType);
                }

            } catch (MessagingException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                Logger.getLogger(GmailApiManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return retList;
    }

    private void addMultiPart(Multipart multiPart, List<GmailMessagePart> retList) throws IOException, MessagingException {
        for (int i = 0; i < multiPart.getCount(); i++) {
            GmailMessagePart gmailMessagePart = new GmailMessagePart();

            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                // this part is attachment
                // code to save attachment...
            } else {
                if (part.getContentType().contains("html")) {
                    gmailMessagePart.html = (String) part.getContent();
                } else if (part.getContentType().contains("multipart/alternative")) {
                    Multipart innerMultiPart = (Multipart) part.getContent();
                    addMultiPart(innerMultiPart, retList);
                } else if (part.getContentType().contains("image/")) {
                    String base64EncodedImage = getImage(part);
                    gmailMessagePart.base64 = base64EncodedImage;
                    gmailMessagePart.contentType = part.getContentType();
                    gmailMessagePart.contentId = part.getContentID();
                } else if (part.getContentType().contains("text")) {
                    gmailMessagePart.text = (String) part.getContent();
                } else {
                    System.out.println("Type not handled: " + part.getContentType());
                }
            }

            retList.add(gmailMessagePart);
        }
    }

    @Override
    public GmailMessageLight getMessageLight(String msgId) {
        GmailMessageLight light = gmailMessages.get(msgId);
        finalizeMessage(light);
        return light;
    }

    private MimeMessage toMimeMessage(String raw) throws MessagingException {
        byte[] emailBytes = Base64.decodeBase64(raw);

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        return new MimeMessage(session, new ByteArrayInputStream(emailBytes));
    }

    private String getImage(MimeBodyPart part) throws IOException, MessagingException {
        if (part instanceof MimeBodyPart) {
            BASE64DecoderStream base64DecoderStream = (BASE64DecoderStream) part.getContent();
            byte[] byteArray = IOUtils.toByteArray(base64DecoderStream);
            byte[] encodeBase64 = Base64.encodeBase64(byteArray);
            return new String(encodeBase64, "UTF-8");

        }

        return "";
    }

    @Override
    public void replyEmail(String msgId, String content) {
        GmailMessageLight lightMsg = getMessageLight(msgId);
        if (lightMsg != null) {
            try {
                MimeMessage msg = createEmail(lightMsg.getFromEmailAddress(), "support@getshop.com", lightMsg.subject, content);
                Message message = createMessageWithEmail(msg);
                message = getService().users().messages().send("me", message).execute();
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private MimeMessage createEmail(String to,
            String from,
            String subject,
            String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        Multipart multipart = new MimeMultipart("mixed");

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(bodyText, "text/html; charset=UTF-8");
        multipart.addBodyPart(messageBodyPart);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        email.setContent(multipart);
        return email;
    }
    
     public Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    @Override
    public void updateTimeSpentOnMessage(String msgId, Integer timeSpent, boolean completed) {
        GmailMessageLight msg = getMessageLight(msgId);
        msg.timeSpent = timeSpent;
        msg.completed = completed;
        saveObject(msg);
    }

    @Override
    public List<GmailMessageLight> getEmails(GmailMessageFilter filter) {
        List<GmailMessageLight> msgs = new ArrayList();
        for(GmailMessageLight msg : gmailMessages.values()) {
            if(filter.userId != null && !msg.isAssignedTo(filter.userId)) {
                continue;
            }
            if(!msg.isUnassigned() && filter.userId == null) {
                continue;
            }
            if(filter.type >= 0 && !filter.type.equals(msg.type)) {
                continue;
            }
            if(filter.completed != msg.completed) {
                continue;
            }
            msgs.add(msg);
        }
        sortAndFinalize(msgs);
        return msgs;

    }

    @Override
    public void changeTypeOnMessage(String messageId, Integer type) {
        GmailMessageLight msg = getMessageLight(messageId);
        msg.type = type;
        saveObject(msg);
    }

    @Override
    public void updateTimeSpenOnMessage(String messageId, Integer time) {
        GmailMessageLight msg = getMessageLight(messageId);
        msg.timeSpent = time;
        saveObject(msg);
    }
}
