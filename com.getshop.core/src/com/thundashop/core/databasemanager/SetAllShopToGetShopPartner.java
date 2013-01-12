package com.thundashop.core.databasemanager;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.ErrorException;
import java.net.UnknownHostException;

/**
 *
 * @author boggi
 */
public class SetAllShopToGetShopPartner {

    public static void main(String args[]) throws UnknownHostException, ErrorException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("StoreManager");
        DBCollection collection = db.getCollection("col_all");
        DBCursor stores = collection.find();
        while (stores.hasNext()) {
            DBObject store = stores.next();
            store.put("partnerId", "GetShop");
            collection.save(store);
        }
    }
}
