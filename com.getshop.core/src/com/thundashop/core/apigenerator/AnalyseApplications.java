package com.thundashop.core.apigenerator;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.thundashop.core.apigenerator.GenerateApi.ApiMethod;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.Database;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.mongodb.morphia.Morphia;

public class AnalyseApplications {

    private final HashMap<String, HashMap<String, List<String>>> toWrite = new HashMap();
    private final GenerateApi generator;
    private final List<Class> allManagers;
    private final List<Class> dataObjects;
    private final List<ApiMethod> allMethods = new ArrayList();
    private final HashMap<Application, List<ApiMethod>> usedMethods = new HashMap();
    String appPath = "../com.getshop.client/app/";
    
    public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
        GenerateApi api = new GenerateApi();
        api.analyseApplications();
    }

    AnalyseApplications(GenerateApi generator, List<Class> managers, List<Class> dataObjects) {
        this.generator = generator;
        this.allManagers = managers;
        this.dataObjects = dataObjects;
    }

    void generate() throws UnknownHostException, IOException {
        for(Class core : allManagers) {
            allMethods.addAll(generator.getMethods(core));
        }
        
        List<Application> allApps = fetchApplicationSettings();
        analyseApplications(allApps);
    }

    private List<Application> fetchApplicationSettings() throws UnknownHostException {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        
        List<Application> result = new ArrayList();
        Mongo m = new Mongo("localhost", Database.mongoPort);
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
                    result.add(dobj);
                }
            }
        }
        return result;
    }

    private void analyseApplications(List<Application> allApps) throws IOException {
        for(Application setting : allApps) {
            String path = this.appPath + "ns_"+setting.id.replace("-", "_");
            analyseDir(new File(path), setting);
        }
        for(Application setting : usedMethods.keySet()) {
            
            if(!toWrite.containsKey(setting.id)) {
                toWrite.put(setting.id, new HashMap());
            }
            
            for(ApiMethod method : usedMethods.get(setting)) {
                String name = method.manager.getCanonicalName().replace(".I", ".");
                if(!toWrite.get(setting.id).containsKey(name)) {
                    toWrite.get(setting.id).put(name, new ArrayList());
                }
                toWrite.get(setting.id).get(name).add(method.methodName);
            }
        }
        Gson gson = new Gson();
        String result = gson.toJson(toWrite);
        generator.writeFile(result, "../apitodb.json");
    }

    private void analyseDir(File path, Application app) {
        if(!path.exists() || path.isFile()) {
            System.out.println(appPath + " does not exists or is file");
            return;
        }
        
        for(File file : path.listFiles()) {
            if(file.isDirectory()) {
                analyseDir(file, app);
            } else {
                analyseFile(file, app);
            }
        }
        
    }

    private void analyseFile(File file, Application app) {
        if(file.getAbsolutePath().endsWith(".php") || file.getAbsolutePath().endsWith(".phtml")) {
            String content = generator.readContent(file.getAbsolutePath());
            for(ApiMethod method : allMethods) {
                //getContentManager()->saveContent
                String searchFor = "->get"+method.manager.getSimpleName().substring(1)+"()->"+method.methodName+"(";
                if(content.indexOf(searchFor) >= 0) {
                    addUsedMethod(app, method);
                }
            }
        }
        
    }

    private void addUsedMethod(Application app, ApiMethod method) {
        if(!usedMethods.containsKey(app)) {
            usedMethods.put(app, new ArrayList());
        }
        usedMethods.get(app).add(method);
    }
}
