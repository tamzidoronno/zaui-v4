/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.mongodb.*;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.StoreComponent;
import com.thundashop.repository.db.MongoClientProvider;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
@Component
public class OAuthDatabase extends StoreComponent {

    private static Logger logger = LoggerFactory.getLogger(OAuthDatabase.class);

    private volatile Mongo mongo;
    private final Morphia morphia;

    private final String collectionPrefix = "col_";

    private final MongoClientProvider mongoClientProvider;
    
    @Autowired
    public OAuthDatabase(@Qualifier("oAuthMongo") MongoClientProvider provider) {
        this.mongoClientProvider = provider;
        morphia = new Morphia();
        morphia.map(DataCommon.class);
    }

    private void connect(){
        mongo = mongoClientProvider.getMongoClient();
    }
    

    private void checkId(DataCommon data) throws ErrorException {
        if (data.id == null || data.id.isEmpty()) {
            data.id = UUID.randomUUID().toString();
        }
    }

    private void addDataCommonToDatabase(DataCommon data) {
        data.gs_manager = "oauth";
        DBObject dbObject = morphia.toDBObject(data);
        connect();
        mongo.getDB("oauth").getCollection(collectionPrefix + "all").save(dbObject);
    }

    public void hardDelete(DataCommon data) throws ErrorException {
        data.gs_manager = "oauth";
        connect();
        mongo.getDB("oauth").getCollection(collectionPrefix + "all").remove(new BasicDBObject().append("_id", data.id));
    }
    
    public void delete(DataCommon data) throws ErrorException {
        data.deleted = new Date();
        save(data);
    }

    public Stream<DataCommon> getAll(String dbName, String storeId, String moduleName) {
        
        try {
            connect();
            
            DBCollection col = mongo.getDB(dbName).getCollection("col_all_" + moduleName);
            Stream<DataCommon> retlist = col.find().toArray().stream()
                    .map(o -> morphia.fromDBObject(DataCommon.class, o));
            return retlist;
        } catch (Exception ex) {
            logger.error("", ex);
        }
        
        return null;
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
            logger.error("", ex);
        }
    }
    
    public List<DataCommon> query(DBObject query) {
        
        try  {
            connect();
            
            DB db = mongo.getDB("oauth");
            DBCollection col = db.getCollection("col_all");
            DBCursor res = col.find(query);
            List<DataCommon> retObjecs = new ArrayList<>();

            while (res.hasNext()) {
                DBObject nx = res.next();
                try {
                    DataCommon data = morphia.fromDBObject(DataCommon.class, nx);
                    retObjecs.add(data);
                } catch (Exception ex) {
                    System.out.println("failed ot map: " + nx.get("_id"));
                }
            }

            return retObjecs;
        } catch (Exception ex) {
            logger.error("", ex);
        }
        
        return new ArrayList<>();
    }
}
