package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsLetterManager extends ManagerBase  implements INewsLetterManager {
    
    public List<NewsLetterGroup> groups = new ArrayList();

    @Autowired
    MailFactory mailFactory;
    
    @Autowired
    public NewsLetterManager(Logger log, DatabaseSaver databaseSaver) {
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
    
    
    @Override
    public void sendNewsLetter(NewsLetterGroup group) throws ErrorException {
        if(group.emailBody == null || group.emailBody.trim().isEmpty()) {
            throw new ErrorException(1019);
        }
        if(group.emails.isEmpty()) {
            throw new ErrorException(1019);
        }
        groups.add(group);
        group.storeId = "all";
        databaseSaver.saveObject(group, credentials);
    }
    
    @Scheduled(fixedRate=3000)
    public void scheduledMailSending() throws ErrorException {
        List<NewsLetterGroup> toRemove = new ArrayList();
        for(NewsLetterGroup group : groups) {
            if(group.emails.isEmpty()) {
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

    @Override
    public void sendNewsLetterPreview(NewsLetterGroup group) throws ErrorException {
        sendNewsLetterGroup(group);
    }

    private void sendEmail(String email, String title, String body) {
        mailFactory.send("post@getshop.com", email, title, body);
    }

    private void sendNewsLetterGroup(NewsLetterGroup group) throws ErrorException {
        String email = group.emails.get(0);
        String body = group.emailBody;
        group.emails.remove(email);
        group.SentMailTo.add(email);
        sendEmail(email, group.title, body);
        databaseSaver.saveObject(group, credentials);
    }
    
}
