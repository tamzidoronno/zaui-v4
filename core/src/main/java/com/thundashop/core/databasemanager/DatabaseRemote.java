/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.getshop.scope.GetShopSession;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.StoreComponent;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.start.Runner;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class DatabaseRemote extends StoreComponent {

    private static final Logger log = LoggerFactory.getLogger(DatabaseRemote.class);

    private volatile Mongo mongo;
    private final MongoClientProvider localMongoProvider;
    private final MongoClientProvider remoteMongoProvider;
    private final Morphia morphia;
    
    private static final String collectionPrefix = "col_";

    @Autowired
    public DatabaseRemote(@Qualifier("localMongo") MongoClientProvider localMongoProvider,
                          @Qualifier("remoteMongo") MongoClientProvider remoteMongoProvider) {
        try {
            createDataFolder();
        } catch (IOException ex) {
            log.error("", ex);
        }

        this.localMongoProvider = localMongoProvider;
        this.remoteMongoProvider = remoteMongoProvider;
        morphia = new Morphia();
        morphia.map(DataCommon.class);
    }

    private void connect() {
        mongo = remoteMongoProvider.getMongoClient();
    }

    private void connectLocal() {
        mongo = localMongoProvider.getMongoClient();
    }

    private void checkId(DataCommon data) throws ErrorException {
        if (data.id == null || data.id.isEmpty()) {
            data.id = UUID.randomUUID().toString();
        }
    }

    private void createDataFolder() throws IOException {
        File file = new File("data");

        if (file.exists() && file.canWrite() && file.isDirectory()) {
            return;
        }

        if (file.exists() && !file.isDirectory()) {
            log.error("The file " + file.getPath() + " is not a folder");
            System.exit(-1);
        }

        file.mkdir();

        if (!file.exists()) {
            log.error("Was not able to create folder `{}`", file.getCanonicalPath());
            System.exit(-1);
        }

    }

    private void addDataCommonToDatabase(DataCommon data, Credentials credentials) {
        if (!GetShopLogHandler.isDeveloper) {
            return;
        }
        
        if (!Runner.AllowedToSaveToRemoteDatabase) {
            log.warn("WARNING!!!!!!!!!!!!!!!! Data is not stored to remote database because remote activation is not activated");
            return;
        }
        
        data.gs_manager = credentials.manangerName;
        DBObject dbObject = morphia.toDBObject(data);
        connect();
        mongo.getDB(credentials.manangerName).getCollection(collectionPrefix + data.storeId).save(dbObject);
    }

    public void delete(DataCommon data, Credentials credentials) throws ErrorException {
        data.deleted = new Date();
        save(data, credentials);
    }

    public List<DataCommon> getAll(String dbName, String storeId, String moduleName) {
        String key = dbName+"_"+storeId+"_"+moduleName;
        
        synchronized(DatabaseRemoteCache.class) {
            if (Runner.cached.get(key) != null) {
                return Runner.cached.get(key);
            }

            try {
                if (GetShopLogHandler.isDeveloper) {
                    connectLocal();
                } else {
                    connect();
                }

                DBCollection col = mongo.getDB(dbName).getCollection("col_all_" + moduleName);
                Stream<DataCommon> retlist = col.find().toArray().stream()
                        .map(o -> morphia.fromDBObject(DataCommon.class, o));

                Runner.cached.put(key, retlist.collect(Collectors.toList()));
            } catch (Exception ex) {
                log.error("", ex);
            }

            return Runner.cached.get(key);
        }
    }

    /**
     * ************** SAVE FUNCTIONS ****************
     */
    public void save(DataCommon data, Credentials credentials) throws ErrorException {
        if (data.rowCreatedDate == null) {
            data.rowCreatedDate = new Date();
        }

        try {
            checkId(data);
            data.onSaveValidate();
            addDataCommonToDatabase(data, credentials);
        } catch (Exception ex) {
            log.error("", ex);
        }
    }
}
