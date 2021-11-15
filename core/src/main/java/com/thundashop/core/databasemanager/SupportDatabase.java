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
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.StoreComponent;
import com.thundashop.core.start.Runner;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SupportDatabase extends StoreComponent {

    private MongoClient mongo;
    private Morphia morphia;
    
    private String collectionPrefix = "col_";
    
    @Autowired
    public Logger logger;
    

    @Autowired
    public SupportDatabase(@Qualifier("supportMongo") MongoClientProvider provider) {
        morphia = new Morphia();
        morphia.map(DataCommon.class);
        
        List<MongoCredential> creds = new ArrayList<>();
        creds.add(MongoCredential.createCredential("getshop", "SupportManager", "aisdfjoiw3j4q2oaijsdfoiajsfdoi23joiASD__ASDF".toCharArray()));
        mongo = provider.getMongoClient();
    }

    private boolean isConnected() {
        try {
            mongo.getAddress();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Support MongoDB is down");
        }
    }

    private void checkId(DataCommon data) throws ErrorException {
        if (data.id == null || data.id.isEmpty()) {
            data.id = UUID.randomUUID().toString();
        }
    }

    private void addDataCommonToDatabase(DataCommon data) {
        isConnected();
        data.gs_manager = "SupportManager";
        DBObject dbObject = morphia.toDBObject(data);
        mongo.getDB("SupportManager").getCollection(collectionPrefix + "all").save(dbObject);
    }

    public void delete(DataCommon data) throws ErrorException {
        data.deleted = new Date();
        save(data);
    }

    public Stream<DataCommon> getAll(String dbName, String storeId, String moduleName) {
        isConnected();
        DBCollection col = mongo.getDB(dbName).getCollection("col_all_" + moduleName);
        Stream<DataCommon> retlist = col.find().toArray().stream()
                .map(o -> morphia.fromDBObject(DataCommon.class, o));
        return retlist;
    }

    /**
     * ************** SAVE FUNCTIONS ****************
     */
    public void save(DataCommon data) throws ErrorException {
        if (data.rowCreatedDate == null) {
            data.rowCreatedDate = new Date();
        }

        try {
            checkId(data);
            data.onSaveValidate();
            addDataCommonToDatabase(data);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DatabaseRemote.class.getName()).log(Level.WARNING, null, ex);
        }
    }
    
    public List<DataCommon> query(DBObject query) {
        
        isConnected();
        DB db = mongo.getDB("SupportManager");
        DBCollection col = db.getCollection("col_all");
        DBCursor res = col.find(query);
        List<DataCommon> retObjecs = new ArrayList();

        while (res.hasNext()) {
            DBObject nx = res.next();
            DataCommon data = morphia.fromDBObject(DataCommon.class, nx);
            retObjecs.add(data);
        }

        return retObjecs;
    }
}
