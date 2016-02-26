package com.thundashop.core.uuidsecuritymanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.User;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SecurityPool extends ManagerBase {
    HashMap<String, UUIDSecurityObject> objects = new HashMap();

    @PostConstruct
    public void init() {
        isSingleton = true;
        storeId = "all";
        initialize();
    }

    
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon object : data.data) {
            if(object instanceof UUIDSecurityObject) {
                UUIDSecurityObject obj = (UUIDSecurityObject)object;
                objects.put(obj.id, obj);
            }
        }
    }
    
    
    boolean hasAccessToUUID(String uuid, User loggedOnUser, boolean read, boolean write) {
        if(loggedOnUser != null && loggedOnUser.isAdministrator()) {
            return true;
        }
        
        UUIDSecurityObject sec = getSecurityForUUID(uuid);
        
        if(sec == null) { return true; }
        if(loggedOnUser == null && (sec.isEmpty())) { return true; }
        if(loggedOnUser == null) { return false; }
        if(read && write) { return sec.readAccessUsers.contains(loggedOnUser.id) && sec.writeAccessUsers.contains(loggedOnUser.id); }
        if(read) { return sec.readAccessUsers.contains(loggedOnUser.id); }
        if(write) { return sec.writeAccessUsers.contains(loggedOnUser.id); }
        
        return false;
    }
    
    public void updateAccessToUUID(String uuid, String userId, boolean read, boolean write) {
        UUIDSecurityObject sec = getSecurityForUUID(uuid);
        if(sec == null) {
            sec = new UUIDSecurityObject();
            sec.id = uuid;
            sec.readAccessUsers.remove(userId);
            sec.writeAccessUsers.remove(userId);
            
            if(read) { sec.readAccessUsers.add(userId); }
            if(write) { sec.writeAccessUsers.add(userId); }
            objects.put(sec.id, sec);
            saveObject(sec);
        }
    }

    private UUIDSecurityObject getSecurityForUUID(String uuid) {
        return objects.get(uuid);
    }
    
}
