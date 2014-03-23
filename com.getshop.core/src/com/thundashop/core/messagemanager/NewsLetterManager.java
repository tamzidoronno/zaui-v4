package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.usermanager.UserManager;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class NewsLetterManager extends ManagerBase implements INewsLetterManager {
    
    @Autowired
    NewsLetterSender sender;    
    
    @Autowired
    public NewsLetterManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
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
        
        
        sender.addGroup(group);
    }

    @Override
    public void sendNewsLetterPreview(NewsLetterGroup group) throws ErrorException {
        group.users = new HashMap();
        String userid = group.userIds.get(0);
        UserManager manager = getManager(UserManager.class);
        group.users.put(userid, manager.getUserById(userid));
        sender.sendNewsLetterGroup(group);
    }

}
