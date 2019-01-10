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
import com.mongodb.MongoClientURI;
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
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SupportDatabase extends StoreComponent {

    public static int mongoPort = 27017;

    private Mongo mongo;
    private Morphia morphia;
    
    private String collectionPrefix = "col_";
    
    @Autowired
    public Logger logger;
    

    public SupportDatabase() throws UnknownHostException {
        morphia = new Morphia();
        morphia.map(DataCommon.class);
    }

    private void connect() throws UnknownHostException {
        String connectionString = "mongodb://getshop:aisdfjoiw3j4q2oaijsdfoiajsfdoi23joiASD__ASDF@192.168.100.1/SupportManager";
        mongo = new MongoClient(new MongoClientURI(connectionString));
    }
    

    private void checkId(DataCommon data) throws ErrorException {
        if (data.id == null || data.id.isEmpty()) {
            data.id = UUID.randomUUID().toString();
        }
    }

    private void addDataCommonToDatabase(DataCommon data) {
        data.gs_manager = "SupportManager";
        DBObject dbObject = morphia.toDBObject(data);
        try {
            connect();
            mongo.getDB("SupportManager").getCollection(collectionPrefix + "all").save(dbObject);
            mongo.close();
        } catch (com.mongodb.CommandFailureException ex) {
            mongo.close();
            ex.printStackTrace();
        } catch (UnknownHostException ex) {
            java.util.logging.Logger.getLogger(DatabaseRemote.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                mongo.close();
            } catch (Exception ex) {
                // Will, then there is not much more we can do?
            }
        }
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
            mongo.close();
            return retlist;
        } catch (Exception ex) {
            mongo.close();
            java.util.logging.Logger.getLogger(DatabaseRemote.class.getName()).log(Level.WARNING, null, ex);
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
            java.util.logging.Logger.getLogger(DatabaseRemote.class.getName()).log(Level.WARNING, null, ex);
        }
    }
    
    public List<DataCommon> query(DBObject query) {
        
        try  {
            connect();
            
            DB db = mongo.getDB("SupportManager");
            DBCollection col = db.getCollection("col_all");
            DBCursor res = col.find(query);
            List<DataCommon> retObjecs = new ArrayList();

            while (res.hasNext()) {
                DBObject nx = res.next();
                DataCommon data = morphia.fromDBObject(DataCommon.class, nx);
                retObjecs.add(data);
            }

            mongo.close();
            return retObjecs;
        } catch (Exception ex) {
            mongo.close();
            java.util.logging.Logger.getLogger(DatabaseRemote.class.getName()).log(Level.WARNING, null, ex);
        }
        
        return new ArrayList();
    }
}
