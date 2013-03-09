package com.thundashop.core.loggermanager;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.loggermanager.data.LoggerData;
import com.thundashop.core.common.JsonObject2;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@Scope("prototype")
public class LoggerManager extends ManagerBase {
    
    @Autowired
    public LoggerManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @PostConstruct
    public void init() {
        initialize();
    }
    
    public void logApiCall(JsonObject2 object) throws ErrorException {
        LoggerManagerThread thread = new LoggerManagerThread();
        thread.object = object;
        thread.databaseSaver = databaseSaver;
        thread.storeId = storeId;
        thread.credentials = credentials;
        
        thread.start();
    }
}

class LoggerManagerThread extends Thread {
    public JsonObject2 object;
    public String storeId;
    public DatabaseSaver databaseSaver;
    public Credentials credentials;
    
    @Override
    public void run() {
        try {
            if(object.interfaceName.equals("core.chat.ChatManager") && object.method.equals("getMessages")) {
                return;
            }
            if(object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("initializeStore")) {
                return;
            }

            LoggerData data = new LoggerData();
            data.data = object;
            data.type = LoggerData.Types.API;
            data.storeId = storeId;
            databaseSaver.saveObject(data, credentials);
        } catch (ErrorException ex) {
            java.util.logging.Logger.getLogger(LoggerManagerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
