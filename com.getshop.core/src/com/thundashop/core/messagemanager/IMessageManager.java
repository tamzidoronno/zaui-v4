package com.thundashop.core.messagemanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

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
     * Get how many messages a user has sent.
     * 
     * @param year
     * @param month
     * @return 
     */
    @Administrator
    public int getSmsCount(int year, int month);
}
