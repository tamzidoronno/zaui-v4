package com.thundashop.core.messagemanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorMessage;
import com.thundashop.core.common.GetShopApi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void sendMail(String to, String toName, String subject, String content, String from, String fromName);
    
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
    public void sendMailWithAttachments(String to, String toName, String subject, String content, String from, String fromName, HashMap<String,String> attachments);
    
    @Administrator
    public List<SmsLogEntry> getSmsLog();
    
    /**
     * Get how many messages a user has sent.
     * 
     * @param year
     * @param month
     * @return 
     */
    @Administrator
    public int getSmsCount(int year, int month);
    
    public void collectEmail(String email);
    
    public List<String> getCollectedEmails();
    
}
