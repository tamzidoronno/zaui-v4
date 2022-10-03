package com.thundashop.core.messagemanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Send emails using the messagemanager.
 */
@GetShopApi
public interface IMessageManager  {
    /**
     * Send a mail.
     * @param to The address to send to
     * @param toName The name of the one receiving it.
     * @param subject The subject of the mail.
     * @param content The content to send
     * @param from The email sent from.
     * @param fromName The name of the sender.
     */
    public String sendMail(String to, String toName, String subject, String content, String from, String fromName);
    
    /**
     * Sending a mail with attachments, 
     * 
     * Map<Key, Value> - Key = FileName in attchments, Value = Base64 encoded stuff
     * 
     * @param to
     * @param toName
     * @param subject
     * @param content
     * @param from
     * @param fromName
     * @param attachments 
     */
    public String sendMailWithAttachments(String to, String toName, String subject, String content, String from, String fromName, HashMap<String,String> attachments);

    /**
     * Get how many messages a user has sent.
     * 
     * @param year
     * @param month
     * @return 
     */
    @Administrator
    public int getSmsCount(int year, int month);
    
    @Administrator
    public void collectEmail(String email);
    
    @Administrator
    public List<String> getCollectedEmails();
    
    @Administrator
    public MailMessage getMailMessage(String mailMessageId);
    
    @Administrator
    public SmsMessage getSmsMessage(String smsMessageId);
    
    @Administrator
    public List<SmsMessage> getAllSmsMessages(Date start, Date end);
    
    @Administrator
    List<SmsMessage> getSmsMessagesSentTo(String prefix, String phoneNumber, Date fromDate, Date toDate);

    public void sendMessageToStoreOwner(String message, String subject);
    
    @Administrator
    public List<MailMessage> getMailSent(Date from, Date to, String toEmailAddress);
    
    public void saveIncomingMessage(SmsMessage message, String code);
    
    @Administrator
    public SmsMessagePage getIncomingMessages(int pageNumber);
    
    public void sendErrorNotify(String inText);
}
