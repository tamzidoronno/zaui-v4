/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.storemanager.data.Store;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class DatabaseSaverImpl implements DatabaseSaver {
    @Autowired
    public Logger logger;
    
    @Autowired
    public Database database;
    

    
    private void registerIdToDataObject(DataCommon data, Credentials credentials) {
        if (data.id == null || data.id.equals(""))
            data.id = UUID.randomUUID().toString();
    }
    
    private void checkStoreId(DataCommon data) throws ErrorException {
        if (!(data instanceof Store) && (data.storeId == null || data.storeId.equals(""))) {
            throw new ErrorException(25);
        }
        if (data instanceof Store && !data.storeId.equals("all")) {
            Exception ex = new Exception("Store id is not equal to col_all");
            data.storeId = "all";
            logger.error(this, "store id for store object is not valid" , ex);
        }
    }
        
    @Override
    public void saveObject(DataCommon data, Credentials credentials) throws ErrorException {
        checkStoreId(data);
        logger.debug(this, "Sending message for saving for, "
                + "from: " + credentials.manangerName 
                + " Object: " + data.getClass().getSimpleName());
        
        registerIdToDataObject(data, credentials);
        database.save(data, credentials);
    }

    @Override
    public void deleteObject(DataCommon data, Credentials credentials) throws ErrorException {
        checkStoreId(data);
        database.delete(data, credentials);
    }

    @Override
    public DataCommon getDatabaseObject(DataCommon data, Credentials credentials) throws ErrorException {
        return database.findObject(data.id, credentials.manangerName);
    }
}
