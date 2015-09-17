/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class MessageStatusFactory {
    @Autowired
    public Database database;

    private Credentials credentials = new Credentials(MessageStatusFactory.class);
            
    public MessageStatusFactory() {
        credentials.manangerName = "MessageStatusFactory";
        credentials.password = UUID.randomUUID().toString();
    }
    
    
    public synchronized void logStatus(String id, String status, String text, String storeId, String messageType) {
          logStatus(id, status,  text, storeId, null, messageType);
    }
    
    public synchronized MailStatus getStatus(String id, String storeId) {
        MailStatus mailSatus = getMailStatus(id, storeId);
        
        if (mailSatus != null) {
            return mailSatus.clone();
        }
        
        return null;
    }
  
    private  MailStatus getMailStatus(String id, String storeId) {
        credentials.storeid = storeId;
        MailStatus mailSatus = database.getById(id, credentials);
        return mailSatus;
    }

    void logStatus(String id, String status, String text, String storeId, Exception ex, String messageType) {
        MailStatus dbStatus = getMailStatus(id, storeId);
        if (dbStatus == null) {
            dbStatus = new MailStatus();
            dbStatus.id = id;
        }
        
        dbStatus.status = status;
        dbStatus.text = text;
        dbStatus.messageType = messageType;
        
        if (ex != null) {
            dbStatus.exeptionMessage = ex.getMessage();
        }
        
        credentials.storeid = storeId;
        dbStatus.storeId = storeId;
        try {
            database.save(dbStatus, credentials);
        } catch (ErrorException ex2) {
            ex.printStackTrace();
            ex2.printStackTrace();
        }     
    }
}
