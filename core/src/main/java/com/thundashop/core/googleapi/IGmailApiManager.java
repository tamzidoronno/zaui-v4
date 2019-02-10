/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.googleapi;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import java.io.IOException;
import java.util.List;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IGmailApiManager {
    
    @ForceAsync
    public void fetchAllMessages();
    
    @Administrator
    public List<GmailMessageLight> getLightMessages(String companyId);
    
    @Administrator
    public List<GmailMessageLight> getAllUnassignedMessages();
    
    @Customer
    public List<GmailMessageLight> getMyUnsolvedMessages();
    
    @Administrator
    public List<GmailMessageLight> getEmails(GmailMessageFilter filter);
    
    @Administrator
    public void assignMessageToUser(String messageId, String userId);
    
    @Administrator
    public void changeTypeOnMessage(String messageId, Integer type);
    
    @Administrator
    public void reScanCompanyConnection();
    
    /**
     * Will create an emaploye with the name and email address
     * and rescan for changes.
     * 
     * @param companyId 
     */
    @Administrator
    public void connectMessageToCompany(String msgId, String companyId);
    
    @Administrator
    public void markAsArchived(String msgId);
    
    @Administrator
    public List<GmailMessagePart> getMessageParts(String id);
    
    @Administrator
    public GmailMessageLight getMessageLight(String msgId);
    
    @Administrator
    public void replyEmail(String msgId, String content);
    
    @Administrator
    public void updateTimeSpentOnMessage(String msgId, Integer timeSpent, boolean completed);
}
