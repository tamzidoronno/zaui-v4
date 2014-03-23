/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsLetterSender extends ManagerBase {
    public List<NewsLetterGroup> groups = new ArrayList();

    @Autowired
    MailFactory mailFactory;
    
    @Autowired
    public NewsLetterSender(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon tmpData : data.data) {
            if(tmpData instanceof NewsLetterGroup) {
                groups.add((NewsLetterGroup) tmpData);
            }
        }
    }

    public void sendNewsLetterGroup(NewsLetterGroup group) throws ErrorException {
        String userId = group.userIds.get(0);
        User user = group.users.get(userId);
        String body = group.emailBody;
        if(user != null) {
            body = body.replaceAll("(?i)\\{Contact.name\\}", user.fullName);
        }
        group.userIds.remove(userId);
        group.SentMailTo.add(userId);
        sendEmail(userId, group.title, body);
        databaseSaver.saveObject(group, credentials);
    }
    
        
    @Scheduled(fixedRate=300000)
    public void scheduledMailSending() throws ErrorException {
        List<NewsLetterGroup> toRemove = new ArrayList();
        for(NewsLetterGroup group : groups) {
            if(group.userIds.isEmpty()) {
                toRemove.add(group);
                continue;
            }
            sendNewsLetterGroup(group);
        }
        for(NewsLetterGroup remove : toRemove) {
            groups.remove(remove);
            databaseSaver.deleteObject(remove, credentials);
        }
    }

    
    public void sendEmail(String email, String title, String body) {
        mailFactory.send("post@getshop.com", email, title, body);
    }
    

    void addGroup(NewsLetterGroup group) throws ErrorException {
        groups.add(group);
        group.storeId = "all";
        databaseSaver.saveObject(group, credentials);
    }

    
}
