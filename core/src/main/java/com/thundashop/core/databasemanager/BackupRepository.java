/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopIgnoreBackup;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.Morphia;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class BackupRepository {
//    public static int mongoPort = 27000;
//    
//    private Mongo mongo;
//    private Morphia morphia;

    public BackupRepository() {
//        try {
//            mongo = new MongoClient("localhost", mongoPort);
//            
//            morphia = new Morphia();
//            morphia.map(DataCommon.class);   
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
    
    public void saveBackup(String userId, DataCommon oldObject, String storeId, String database, String collection) {
//        if (oldObject == null) {
//            return;
//        }
//        
//        GetShopIgnoreBackup ignoreLogging = oldObject.getClass().getAnnotation(GetShopIgnoreBackup.class);
//        
//        if (ignoreLogging != null) {
//            return;
//        }
//        
//        BackupRepositoryWorker backupWorker = new BackupRepositoryWorker(userId, oldObject, mongo, morphia, storeId, database, collection);
//        new Thread(backupWorker).start();
    }

    public List<DataCommon> query(DBObject dbObject, String storeId) {
        return new ArrayList();
//        DB db = mongo.getDB("backup");
//        DBCollection col = db.getCollection("col_" + storeId);
//        DBCursor cur = col.find(dbObject);
//        
//        List<DataCommon> dataRet = new ArrayList();
//        while(cur.hasNext()) {
//            DBObject obj = cur.next();
//            DataCommon data = morphia.fromDBObject(DataCommon.class, obj);
//            dataRet.add(data);
//        }
//        
//        return dataRet;
    }

}
