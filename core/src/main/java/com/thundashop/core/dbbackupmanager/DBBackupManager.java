/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.dbbackupmanager;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DataCommonBackup;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.BackupRepository;
import com.thundashop.core.databasemanager.Database;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class DBBackupManager extends ManagerBase implements IDBBackupManager {

    @Autowired
    private BackupRepository backupRepository;

    @Autowired
    private Database database;

    @Override
    public List<DBChange> getChanges(String className) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("originalClassName", className);
        List<DBChange> changes = collectChanges(dbObject);
        return changes;
    }

    private List<DBChange> collectChanges(BasicDBObject dbObject) {
        List<DataCommon> retData = backupRepository.query(dbObject, storeId);
        List<DBChange> changes = retData.stream().map(o -> new DBChange(o)).collect(Collectors.toList());
        Collections.sort(changes, new Comparator<DBChange>() {
            public int compare(DBChange m1, DBChange m2) {
                return m2.timeStamp.compareTo(m1.timeStamp);
            }
        });
        return changes;
    }

    @Override
    public List<DBChange> getChangesById(String className, String id) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("originalClassName", className);
        dbObject.put("originalId", id);
        List<DBChange> changes = collectChanges(dbObject);
        return changes;
    }

    @Override
    public String getDiff(String className, String id1, String id2) {
        DataCommon objectHead = getHeadObject(id1, id2);

        DataCommon object1 = null;
        DataCommon object2 = null;

        if (objectHead != null) {
            object1 = objectHead;
            object2 = getNotHead(id1, id2).oldObject;
        } else {
            object1 = getObject(id1).oldObject;
            object2 = getObject(id2).oldObject;
        }

        String gson1 = getJson(object1);
        String gson2 = getJson(object2);

        return createDiff(gson1, gson2);
    }

    private DataCommon getHeadObject(String id1, String id2) {
        if (!id1.equals("HEAD") && !id2.equals("HEAD")) {
            return null;
        }

        if (!id1.equals("HEAD")) {
            DataCommonBackup object1 = getObject(id1);
            return database.findObject(object1.originalId, object1.database);
        }

        if (!id2.equals("HEAD")) {
            DataCommonBackup object1 = getObject(id2);
            return database.findObject(object1.originalId, object1.database);
        }

        return null;
    }

    private DataCommonBackup getObject(String id) {
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("_id", id);
        return (DataCommonBackup) backupRepository.query(dbObject, storeId).stream().findFirst().orElse(null);
    }

    private DataCommonBackup getNotHead(String id1, String id2) {
        if (id1.equals("HEAD")) {
            return getObject(id2);
        }

        if (id2.equals("HEAD")) {
            return getObject(id1);
        }

        return null;
    }

    private String getJson(DataCommon object1) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object1);
    }

    private synchronized String createDiff(String gson1, String gson2) {
        writeContent(gson1, "/tmp/gs_file_compare1.txt");
        writeContent(gson2, "/tmp/gs_file_compare2.txt");
        
        String data = "";
        try {
            data = readData();
        } catch (IOException ex) {
            Logger.getLogger(DBBackupManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(DBBackupManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }

    private void writeContent(String gson1, String fileName) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"))) {
            writer.write(gson1);
        } catch (IOException ex) {
            Logger.getLogger(DBBackupManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String readData() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("diff /tmp/gs_file_compare1.txt /tmp/gs_file_compare2.txt");
        InputStream stdin = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        String lineToReturn = "";


        while ( (line = br.readLine()) != null)
            lineToReturn += line+"<br/>";


        int exitVal = proc.waitFor();            
        return lineToReturn;
    }

}
