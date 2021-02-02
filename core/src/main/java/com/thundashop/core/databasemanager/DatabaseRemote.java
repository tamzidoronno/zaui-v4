/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
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
import static com.thundashop.core.databasemanager.Database.mongoPort;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.start.Runner;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class DatabaseRemote extends StoreComponent {

    public static int mongoPort = 27017;

    private Mongo mongo;
    private Morphia morphia;
    
    private String collectionPrefix = "col_";
    
    public static HashMap<String, List<DataCommon>> cached = new HashMap();
    
    @Autowired
    public Logger logger;
    
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
            System.out.println("Warning, you do not have the commonpassword.txt file on your computer, you will not be able to write to the common database. file should be located ../commonpassword.txt, next to the secret.txt");
        }
        
        return new String[0];
    }

    public DatabaseRemote() throws UnknownHostException {
        try {
            createDataFolder();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
        morphia = new Morphia();
        morphia.map(DataCommon.class);

    }

    private void connect() throws UnknownHostException {
        String connectionString = "mongodb://getshopreadonly:readonlypassword@192.168.100.1/admin";
        
        if (GetShopLogHandler.isDeveloper) {
            String[] linesFromFile = readLines("../commonpassword.txt");
            if (linesFromFile != null && linesFromFile.length > 0) {
                connectionString = linesFromFile[0];
            }
        }
        
        mongo = new MongoClient(
                new MongoClientURI(connectionString)
        );
    }
    
    private void connectLocal() throws UnknownHostException {
        mongo = new Mongo("localhost", Database.mongoPort);
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
            GetShopLogHandler.logPrintStatic("The file " + file.getPath() + " is not a folder", null);
            System.exit(-1);
        }

        file.mkdir();

        if (!file.exists()) {
            GetShopLogHandler.logPrintStatic("=======================================================================================================", null);
            GetShopLogHandler.logPrintStatic("Was not able to create folder " + file.getCanonicalPath(), null);
            GetShopLogHandler.logPrintStatic("=======================================================================================================", null);
            System.exit(-1);
        }

    }

    private void addDataCommonToDatabase(DataCommon data, Credentials credentials) {
        if (!GetShopLogHandler.isDeveloper) {
            return;
        }
        
        if (!Runner.AllowedToSaveToRemoteDatabase) {
            System.out.println("WARNING!!!!!!!!!!!!!!!! Data is not stored to remote database because remote activation is not activated");
            return;
        }
        
        data.gs_manager = credentials.manangerName;
        DBObject dbObject = morphia.toDBObject(data);
        try {
            connect();
            mongo.getDB(credentials.manangerName).getCollection(collectionPrefix + data.storeId).save(dbObject);
            mongo.close();
        } catch (com.mongodb.CommandFailureException ex) {
            ex.printStackTrace();
        } catch (UnknownHostException ex) {
            java.util.logging.Logger.getLogger(DatabaseRemote.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(DataCommon data, Credentials credentials) throws ErrorException {
        data.deleted = new Date();
        save(data, credentials);
    }

    public List<DataCommon> getAll(String dbName, String storeId, String moduleName) {
        String key = dbName+"_"+storeId+"_"+moduleName;
        
        if (cached.get(key) != null) {
            return cached.get(key);
        }
        
        try {
            if (GetShopLogHandler.isDeveloper) {
                connect();
                //connectLocal();
            } else {
                connect();
            }
            
            long timeUsed = System.currentTimeMillis();
            DBCollection col = mongo.getDB(dbName).getCollection("col_all_" + moduleName);
            Stream<DataCommon> retlist = col.find().toArray().stream()
                    .map(o -> morphia.fromDBObject(DataCommon.class, o));
            mongo.close();
            
            cached.put(key, retlist.collect(Collectors.toList()));
            
            System.out.println("Feched " + key + ", time used: "+ (System.currentTimeMillis() - timeUsed) + " | objects: " + cached.get(key).size());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DatabaseRemote.class.getName()).log(Level.WARNING, null, ex);
        }
        
        return cached.get(key);
    }

    /**
     * ************** SAVE FUNCTIONS ****************
     */
    public void save(DataCommon data, Credentials credentials) throws ErrorException {
//        if (data.rowCreatedDate == null) {
//            data.rowCreatedDate = new Date();
//        }
//
//        try {
//            checkId(data);
//            data.onSaveValidate();
//            addDataCommonToDatabase(data, credentials);
//        } catch (Exception ex) {
//            java.util.logging.Logger.getLogger(DatabaseRemote.class.getName()).log(Level.WARNING, null, ex);
//        }
    }
}
