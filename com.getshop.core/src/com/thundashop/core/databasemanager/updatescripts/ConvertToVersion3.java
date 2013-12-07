package com.thundashop.core.databasemanager.updatescripts;

import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Setting;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageArea;
import com.thundashop.core.productmanager.data.Product;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

public class ConvertToVersion3 {

    public static HashMap<String, AppConfiguration> appConfig = new HashMap();

    public static void main(String[] args) throws UnknownHostException {
        LoadAppConfig();
        MoveApplications();
    }

    private static void MoveApplications() throws MongoException, UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("PageManager");

        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            //Now find pages which helds the apps.
            DBCollection selectedCollection = db.getCollection(colection);
            DBCursor allDocs = selectedCollection.find();
            while (allDocs.hasNext()) {
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, allDocs.next());
                if (dataCommon instanceof Page) {
                    Page page = (Page) dataCommon;
                    if(page.type == 9) {
                        page.moveApplicationToArea("left_1", "right_1");
                        selectedCollection.save(morphia.toDBObject(page));
                    }
                    if(page.type == 14) {
                        page.moveApplicationToArea("left_1", "right_1");
                        page.moveApplicationToArea("left_2", "right_2");
                        selectedCollection.save(morphia.toDBObject(page));
                    }
                    if(page.type == 15) {
                        page.moveApplicationToArea("left_1", "right_1");
                        page.moveApplicationToArea("left_2", "right_2");
                        selectedCollection.save(morphia.toDBObject(page));
                    }
                    if(page.type == 26) {
                        page.removeApplicationOnArea("main_2");
                        page.moveApplicationToArea("col_4", "main_2");
                        selectedCollection.save(morphia.toDBObject(page));
                    }
                }
            }
        }
    }

    private static void LoadAppConfig() throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("PageManager");

        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            //Now find pages which helds the apps.
            DBCollection selectedCollection = db.getCollection(colection);
            DBCursor allDocs = selectedCollection.find();
            while (allDocs.hasNext()) {
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, allDocs.next());
                if (dataCommon instanceof AppConfiguration) {
                    AppConfiguration confgi = (AppConfiguration) dataCommon;
                    appConfig.put(confgi.id, confgi);
                }
            }
        }
    }
}
