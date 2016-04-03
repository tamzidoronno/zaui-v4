/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DataCommonBackup;
import java.util.Date;
import java.util.UUID;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author ktonder
 */
public class BackupRepositoryWorker implements Runnable {
    private final DataCommon oldObject;
    private final String userId;
    private final Morphia morphia;
    private final Mongo mongo;
    private final String storeId;
    private final String collection;
    private final String database;

    BackupRepositoryWorker(String userId, DataCommon oldObject, Mongo mongo, Morphia morphia, String storeId, String database, String collection) {
        this.userId = userId;
        this.mongo = mongo;
        this.morphia = morphia;
        this.oldObject = oldObject;
        this.storeId = storeId;
        this.database = database;
        this.collection = collection;
    }
    
    
    @Override
    public void run() {
        
        DataCommonBackup backup = new DataCommonBackup();
        backup.doneByUserId = userId;
        backup.oldObject = oldObject;
        backup.originalId = oldObject.id;
        backup.originalClassName = oldObject.getClass().getCanonicalName();
        backup.rowCreatedDate = new Date();
        backup.colection = collection;
        backup.database = database;
        backup.id = UUID.randomUUID().toString();
        
        DBObject object = morphia.toDBObject(backup);
        mongo.getDB("backup").getCollection("col_"+storeId).save(object);
    }
}