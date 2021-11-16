/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.getshop.scope.GetShopSession;
import com.mongodb.*;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Mongo mongoLocal;
    private final Morphia morphia;
    
    private static final String collectionPrefix = "col_";

    private static final Map<String, Mongo> mongoCache = new ConcurrentHashMap<>();
    
    private String[] readLines(String filename) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines.toArray(new String[lines.size()]);
        } catch (IOException ex) {
            log.error("Warning, you do not have the commonpassword.txt file on your computer, you will not be able to write to the common database. file should be located ../commonpassword.txt, next to the secret.txt", ex);
        }
        
        return new String[0];
    }

    @Autowired
    public DatabaseRemote(@Qualifier("localMongo") MongoClientProvider localMongoProvider) {
        try {
            createDataFolder();
        } catch (IOException ex) {
            log.error("", ex);
        }

        this.mongoLocal = localMongoProvider.getMongoClient();
        morphia = new Morphia();
        morphia.map(DataCommon.class);

    }

    private void connect() {
        mongo = mongoCache.computeIfAbsent("remoteMongo", k -> connectRemote());
    }

    private Mongo connectRemote() {
        String connectionString = "mongodb://getshopreadonly:readonlypassword@192.168.100.1/admin";

        if (GetShopLogHandler.isDeveloper) {
            String[] linesFromFile = readLines("../commonpassword.txt");
            if (linesFromFile != null && linesFromFile.length > 0) {
                connectionString = linesFromFile[0];
            }
        }

        try {
            return new MongoClient(new MongoClientURI(connectionString));
        } catch (UnknownHostException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    private void connectLocal() {
        mongo = mongoLocal;
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
    //                connect();
                    connectLocal();
                } else {
                    connect();
                }

                long timeUsed = System.currentTimeMillis();
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
