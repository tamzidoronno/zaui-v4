/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;


import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DataScript;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.databasemanager.data.Credentials;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class Database2 {
    
    @Autowired
    public Database databaseSingleton;
    
    /**************** SAVE FUNCTIONS ***************/
    public void save(DataCommon application, Credentials cred) {
        GetShopLogHandler.logPrintStatic("Saving1", null);
        databaseSingleton.save(application, cred);
    }

    void save(String database, String dbscripts, DataScript dbScript) {
        GetShopLogHandler.logPrintStatic("Saving2", null);
        databaseSingleton.save(database, dbscripts, dbScript);
    }
    
    public void save(Class managerClass, DataCommon data) {
        GetShopLogHandler.logPrintStatic("Saving3", null);
        databaseSingleton.save(managerClass, data);
    }
    
    public void save(String simpleName, String colName, DataCommon mailMessage) {
        GetShopLogHandler.logPrintStatic("Saving4", null);
        databaseSingleton.save(simpleName, colName, mailMessage);
    }
    /**************** END SAVE ************/
    
    /**************** DELETE FUNCTIONS *************/
    public void delete(DataCommon application, Credentials cred) {
        GetShopLogHandler.logPrintStatic("Delete1", null);
        databaseSingleton.delete(application, cred);
    }

    public void delete(Class managerClass, DataCommon data) {
        GetShopLogHandler.logPrintStatic("Delete2", null);
        databaseSingleton.delete(managerClass, data);
    }
    /**************** END DELETE ************/
    
    public Stream<DataCommon> getAll(String dbName, String storeId) {
        return databaseSingleton.getAll(dbName, storeId);
    }

    public List<DataCommon> retreiveData(Credentials credentials) {
        return databaseSingleton.retreiveData(credentials);
    }
    
    public DataCommon findObject(String id, String manangerName) {
        return databaseSingleton.findObject(id, manangerName);
    }

    public List<String> getMultilevelNames(String simpleName, String storeId) {
        return databaseSingleton.getMultilevelNames(simpleName, storeId);
    }

    boolean exists(String database, String dbscripts, DataScript dbScript) {
        return databaseSingleton.exists(database, dbscripts, dbScript);
    }

    public Mongo getMongo() {
        return databaseSingleton.getMongo();
    }

    public List<DataCommon> query(String manager, String storeId, BasicDBObject query) {
        return databaseSingleton.query(manager, storeId, query);
    }

    public List<DataCommon> find(String collection, Date start, Date stop, String db, HashMap<String, String> search) {
        return databaseSingleton.find(collection, start, stop, db, search);
    }   

    public List<DataCommon> getAllDataForStore(String storeId) {
        return databaseSingleton.getAllDataForStore(storeId);
    }

    public void refreshDatabase(List<DataCommon> datas) {
        databaseSingleton.refreshDatabase(datas);
    }
}