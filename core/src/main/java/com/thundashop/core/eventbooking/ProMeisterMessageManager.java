/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.messagemanager.IMessageManager;
import com.thundashop.core.messagemanager.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class ProMeisterMessageManager  {
    private String smsFactoryType = "nexmo";
    
    @Autowired
    private MessageManager messageManager;
    
    public String sendMail(Event event, String emailAddress, String fullName, String subject, String content, String email, String from) {
        if (!event.isInFuture()) {
            return "";
        }
        
        return messageManager.sendMail(emailAddress, fullName, subject, content, email, from);
    }

    public String sendSms(Event event, String cellPhone, String content, String prefix) {
        if (!event.isInFuture()) {
            return "";
        }
        
        return messageManager.sendSms(smsFactoryType, cellPhone, content, prefix);
    }

    public String sendSms(Event event, String phoneNumber, String content, String prefix, String storeName) {
        if (!event.isInFuture()) {
            return "";
        }
        
        return messageManager.sendSms(smsFactoryType, phoneNumber, content, prefix, storeName);
    }
    
}
