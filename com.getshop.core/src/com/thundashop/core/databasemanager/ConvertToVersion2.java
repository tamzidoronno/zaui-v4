package com.thundashop.core.databasemanager;

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

public class ConvertToVersion2 {

    public static HashMap<String, Product> allProducts = new HashMap();
    public static HashMap<String, AppConfiguration> appConfig = new HashMap();

    public static void main(String[] args) throws UnknownHostException {
        LoadProducts();
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
                    page.moveApplicationToArea("middle", "main_1");
                    page.moveApplicationToArea("left", "left_1");
                    page.moveApplicationToArea("right", "right_1");

                    Set<String> areas = page.getAllPageAreas();
                    for (String area : areas) {
                        PageArea pagearea = page.getPageArea(area);
                        for (String appid : pagearea.applicationsList) {
                            AppConfiguration config = appConfig.get(appid);
                            if (config != null) {
                                if (config.appSettingsId != null && config.appSettingsId.equals("dcd22afc-79ba-4463-bb5c-38925468ae26")) {
                                    if(!config.settings.containsKey("productid") && allProducts.containsKey(page.id)) {
                                        Setting settings = new Setting();
                                        settings.value = allProducts.get(page.id).id;
                                        settings.type = "productid";
                                        settings.secure = false;
                                        config.settings.put("productid", settings);
                                        selectedCollection.save(morphia.toDBObject(config));
                                    }
                                }
                            }
                        }
                    }
                    selectedCollection.save(morphia.toDBObject(page));
                }
            }
        }
    }

    private static void LoadProducts() throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("ProductManager");

        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            //Now find pages which helds the apps.
            DBCollection selectedCollection = db.getCollection(colection);
            DBCursor allDocs = selectedCollection.find();
            while (allDocs.hasNext()) {
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, allDocs.next());
                if (dataCommon instanceof Product) {
                    Product product = (Product) dataCommon;
                    allProducts.put(product.pageId, product);
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
