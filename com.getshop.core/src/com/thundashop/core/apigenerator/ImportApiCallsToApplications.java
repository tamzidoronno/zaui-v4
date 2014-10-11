package com.thundashop.core.apigenerator;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.thundashop.core.appmanager.data.ApiCallsInUse;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.DataCommon;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mongodb.morphia.Morphia;

public class ImportApiCallsToApplications {

    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException {
        ImportApiCallsToApplications importer = new ImportApiCallsToApplications();
        importer.run();
    }
    
    private HashMap<String, Application> fetchApplicationSettings() throws UnknownHostException {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        HashMap<String, Application> result = new HashMap();
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("ApplicationPool");
        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            //Now find pages which helds the apps.
            DBCollection selectedCollection = db.getCollection(colection);
            DBCursor allDocs = selectedCollection.find();
            while (allDocs.hasNext()) {
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, allDocs.next());
                if (dataCommon instanceof Application) {
                    Application dobj = (Application) dataCommon;
                    result.put(dobj.id, dobj);
                }
            }
        }
        m.close();
        return result;
    }

    public void run() throws UnknownHostException, ClassNotFoundException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("ApplicationPool");
        DBCollection collection = db.getCollection("col_all");
        
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        
        HashMap<String, Application> apps = fetchApplicationSettings();
        GenerateApi tmp = new GenerateApi();
        String jsondata = tmp.readContent("../apitodb.json");
        Gson gson = new Gson();
        
        HashMap<String, HashMap<String, List<String>>> toAdd = new HashMap();
        toAdd = gson.fromJson(jsondata, toAdd.getClass());
        for(String appid : toAdd.keySet()) {
            Map<String, List<String>> list = toAdd.get(appid);
            apps.get(appid).apiCallsInUse = new ArrayList();
            for(String managerPath : list.keySet()) {
                for(String methodName : list.get(managerPath)) {
                    ApiCallsInUse inuse = new ApiCallsInUse();
                    inuse.manager = managerPath;
                    inuse.method = methodName;
                    apps.get(appid).apiCallsInUse.add(inuse);
                }
            }
            
            collection.save(morphia.toDBObject(apps.get(appid)));
        }
        m.close();
    }
}
