package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class NewsLetterManager extends ManagerBase implements INewsLetterManager {
    private List<NewsLetterGroup> groups = new CopyOnWriteArrayList();

    @Autowired
    private NewsLetterSender sender;    
    
    @Autowired
    private MailFactory mailFactory;
 
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon tmpData : data.data) {
            if(tmpData instanceof NewsLetterGroup) {
                groups.add((NewsLetterGroup) tmpData);
            }
        }
    }
    
    @Autowired
    public NewsLetterManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @PostConstruct
    public void addManager() {
        sender.addManager(this);
    }
    
    @Override
    public void sendNewsLetter(NewsLetterGroup group) throws ErrorException {
        if(group.emailBody == null || group.emailBody.trim().isEmpty()) {
            throw new ErrorException(1019);
        }
        if(group.userIds.isEmpty()) {
            throw new ErrorException(1019);
        }
        
        UserManager manager = getManager(UserManager.class);
        
        group.users = new HashMap();
        for(String userId : group.userIds) {
            group.users.put(userId, manager.getUserById(userId));
        }
        
        group.currentStoreId = storeId;
        
        addGroup(group);
    }

    @Override
    public void sendNewsLetterPreview(NewsLetterGroup group) throws ErrorException {
        group.users = new HashMap();
        String userid = group.userIds.get(0);
        UserManager manager = getManager(UserManager.class);
        group.users.put(userid, manager.getUserById(userid));
        sendNewsLetterGroup(group);
    }

    public void run() {
        List<NewsLetterGroup> toRemove = new ArrayList();
        for(NewsLetterGroup group : groups) {
            if(group.userIds.isEmpty()) {
                toRemove.add(group);
                continue;
            }
            
            try {
                sendNewsLetterGroup(group);
            } catch (ErrorException ex) {
                log.error(this, "Could not send newsletter", ex);
            }
        }
        
        for(NewsLetterGroup remove : toRemove) {
            groups.remove(remove);
            
            try {
                databaseSaver.deleteObject(remove, credentials);
            } catch (ErrorException ex) {
                log.error(this, "Could not save newsletter", ex);
            }   
        }
    }
    
    private void sendNewsLetterGroup(NewsLetterGroup group) throws ErrorException {
        String userId = group.userIds.get(0);
        User user = group.users.get(userId);
        String body = group.emailBody;
        if(user != null) {
            body = body.replaceAll("(?i)\\{Contact.name\\}", user.fullName);
        }
        group.userIds.remove(userId);
        group.SentMailTo.add(userId);
        sendEmail(userId, group.title, body, group.currentStoreId);
        databaseSaver.saveObject(group, credentials);
    }
    
    
    private void sendEmail(String email, String title, String body, String storeId) {
        mailFactory.setStoreId(storeId);
        mailFactory.send("post@getshop.com", email, title, body);
    }
    

    private void addGroup(NewsLetterGroup group) throws ErrorException {
        group.storeId = storeId;
        groups.add(group);
        databaseSaver.saveObject(group, credentials);
    }
}
