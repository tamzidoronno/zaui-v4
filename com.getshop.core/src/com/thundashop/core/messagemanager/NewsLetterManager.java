package com.thundashop.core.messagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.utilmanager.data.FileObject;
import com.thundashop.core.utils.UtilManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class NewsLetterManager extends ManagerBase implements INewsLetterManager {
    private List<NewsLetterGroup> groups = new CopyOnWriteArrayList();

    @Autowired
    private NewsLetterSender sender;    
    
    @Autowired
    private MailFactory mailFactory;
    
    @Autowired
    private UserManager userManager;
 
    @Autowired
    private StoreManager storeManager;
 
    @Autowired
    private UtilManager utilManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon tmpData : data.data) {
            if(tmpData instanceof NewsLetterGroup) {
                groups.add((NewsLetterGroup) tmpData);
            }
        }
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
        
        addGroup(group);
    }

    @Override
    public void sendNewsLetterPreview(NewsLetterGroup group) throws ErrorException {
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
        User user = userManager.getUserById(userId);
        String body = group.emailBody;
        if(user != null) {
            body = body.replaceAll("(?i)\\{Contact.name\\}", user.fullName);
        }
        group.userIds.remove(userId);
        group.SentMailTo.add(userId);
        
        String filepath = "/uploadedFile.php?id=";
          if(storeManager.getMyStore().webAddressPrimary != null) {
            filepath = "http://" + storeManager.getMyStore().webAddressPrimary + filepath;
        } else {
            filepath = "http://" + storeManager.getMyStore().webAddress + filepath;
        }
        
        if(!group.attachments.isEmpty()) {
            body += "<hr>";
            for(String attachmentid : group.attachments) {
                FileObject file = utilManager.getFile(attachmentid);
                String attachmentfilepath = filepath + file.id;
                String attachmentEntry = "<div><a href='" + attachmentfilepath + "'>" + file.filename + "</a></div>";
                body += attachmentEntry;
            }
        }
        
        if(user != null) {
            sendEmail(user.emailAddress, group.title, body);
        }
        databaseSaver.saveObject(group, credentials);
    }
    
    private void sendEmail(String email, String title, String body) {
        mailFactory.send("post@getshop.com", email, title, body);
    }
    
    private void addGroup(NewsLetterGroup group) throws ErrorException {
        group.storeId = storeId;
        groups.add(group);
        databaseSaver.saveObject(group, credentials);
    }
}
