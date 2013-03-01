package com.thundashop.core.databasemanager;

import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageArea;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author boggi
 */
public class AddPageSettingsIdToAppConfigurationObject {

    public static void main(String args[]) throws UnknownHostException, ErrorException {
        Mongo m = new Mongo("localhost", 27017);
        //First, find all product lists in the system.
        DB db = m.getDB("PageManager");

        TreeMap<String, String> check = new TreeMap();
        check.put("Account", "6c245631-effb-4fe2-abf7-f44c57cb6c5b");
        check.put("ApplicationDisplayer", "c841f5a5-ecd5-4007-b9da-2c7538c07212");
        check.put("Banner", "d612904c-8e44-4ec0-abf9-c03b62159ce4");
        check.put("Booking", "74ea4e90-2d5a-4290-af0c-230a66e09c78");
        check.put("BreadCrumb", "7093535d-f842-4746-9256-beff0860dbdf");
        check.put("Bring", "2da52bbc-a392-4125-92b6-eec1dc4879e9");
        check.put("Calendar", "6f3bc804-02a1-44b0-a17d-4277f0c6dee8");
        check.put("CartManager", "900e5f6b-4113-46ad-82df-8dafe7872c99");
        check.put("CategoryLister", "e9d04f19-6eaa-4a17-9b7b-aa387dbaed92");
        check.put("Chat", "2afb045b-fa01-4398-8582-33f212bb8db8");
        check.put("ColumnProducts", "de0b9b83-a41d-4fca-a2d4-3d3945cc8b9e");
        check.put("Contact", "96de3d91-41f2-4236-a469-cd1015b233fc");
        check.put("ContentManager", "320ada5b-a53a-46d2-99b2-9b0b26a7105a");
        check.put("CreateStore", "e2554f70-ecdb-47a6-ba37-79497ea65986");
        check.put("Designs", "636149b5-f3c9-4b63-99e1-83eeb5742e05");
        check.put("Dibs", "d02f8b7a-7395-455d-b754-888d7d701db8");
        check.put("Facebook", "ba885f72-f571-4a2e-8770-e91cbb16b4ad");
        check.put("Footer", "d54f339d-e1b7-412f-bc34-b1bd95036d83");
        check.put("Gallery", "e72f97ad-aa1f-4e67-bcfd-e64607f05f93");
        check.put("GoogleAnalytics", "0cf21aa0-5a46-41c0-b5a6-fd52fb90216f");
        check.put("LeftMenu", "00d8f5ce-ed17-4098-8925-5697f6159f66");
        check.put("Login", "df435931-9364-4b6a-b4b2-951c90cc0d70");
        check.put("Logo", "974beda7-eb6e-4474-b991-5dbc9d24db8e");
        check.put("Mail", "8ad8243c-b9c1-48d4-96d5-7382fa2e24cd");
        check.put("MainMenu", "bf35979f-6965-4fec-9cc4-c42afd3efdd7");
        check.put("News", "dabb8a85-f593-43ec-bf0d-240467118a40");
        check.put("OpenSRS", "fb076580-c7df-471c-b6b7-9540e4212441");
        check.put("OrderManager", "27716a58-0749-4601-a1bc-051a43a16d14");
        check.put("PayPal", "c7736539-4523-4691-8453-a6aa1e784fc1");
        check.put("ProductList", "8402f800-1e7e-43b5-b3f7-6c7cabbf8942");
        check.put("ProductManager", "dcd22afc-79ba-4463-bb5c-38925468ae26");
        check.put("Reporting", "04259325-abfa-4311-ab81-b89c60893ce1");
        check.put("Search", "626ff5c4-60d4-4faf-ac2e-d0f21ffa9e87");
        check.put("SedoxCarList", "2ebd7c69-eba3-4b7e-85c6-0e0bd274aad0");
        check.put("Settings", "d755efca-9e02-4e88-92c2-37a3413f3f41");
        check.put("Shipper", "098bb0fe-eb51-42c6-9fbb-dadb7b52dd56");
        check.put("SmsStatistic", "39f1485a-85b8-4f09-ba70-0e33c16f8dc6");
        check.put("StockControl", "a93d64e4-b7fa-4d55-a804-ea664b037e72");
        check.put("TopMenu", "1051b4cf-6e9f-475d-aa12-fc83a89d2fd4");
        check.put("Translation", "ee1f3649-cfd8-41d5-aa5b-682216f376b6");
        check.put("Users", "ba6f5e74-87c7-4825-9606-f2d3c93d292f");
        check.put("WebShopList", "e9864616-96d6-485f-8cb0-e17cdffbcfec");

        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            HashMap<String, AppConfiguration> pool = createApplicationPool(db, colection);

            //Now find pages which helds the apps.
            DBCollection selectedCollection = db.getCollection(colection);
            DBCursor allDocs = selectedCollection.find();
            while (allDocs.hasNext()) {
                DataCommon dataCommon = convert(allDocs.next());
                if (dataCommon instanceof AppConfiguration) {
                    AppConfiguration config = (AppConfiguration)dataCommon;
                    config.appSettingsId = check.get(config.appName);
                    System.out.println("Setting : " + config.id + " : " + check.get(config.appName));
                    selectedCollection.save(morphia.toDBObject(config));
                }
            }
        }


    }

    private static HashMap<String, AppConfiguration> createApplicationPool(DB db, String colection) {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        //First build the application pool
        DBCollection selectedCollection = db.getCollection(colection);
        HashMap<String, AppConfiguration> pool = new HashMap();
        DBCursor allDocs = selectedCollection.find();
        while (allDocs.hasNext()) {
            DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, allDocs.next());
            if (dataCommon instanceof AppConfiguration) {
                AppConfiguration config = (AppConfiguration) dataCommon;
                pool.put(config.id, config);
            }
        }
        return pool;
    }

    private static DataCommon convert(DBObject next) {
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);
        DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, next);
        return dataCommon;
    }
}
