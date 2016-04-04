/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.List;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author ktonder
 */
public class UpdateCertegoOrders extends UpgradeBase {
    
    public static void main(String[] args) throws UnknownHostException {
        new UpdateCertegoOrders().start();
    }

    private void start() throws UnknownHostException {
        Mongo m = new MongoClient("localhost", 27018);
        DB db = m.getDB("CertegoManager");
        
        DBCollection col = db.getCollection("col_1e647711-6624-40fd-807e-7673250accc4");
        
        
        BasicDBObject basicObject = new BasicDBObject();
        basicObject.put("className", "com.thundashop.core.certego.data.CertegoOrders");
        
        DBCursor cur = col.find(basicObject);
        
        BASE64Encoder encoder = new BASE64Encoder();
        
        while(cur.hasNext()) {
            DBObject orderObject = cur.next();
            BasicDBList orders = (BasicDBList) orderObject.get("orders");
            Object[] arr = orders.toArray();
            for (Object obj : arr) {
                DBObject objDB = (DBObject)obj;
                String data = (String)objDB.get("data");
                objDB.put("data", encoder.encode(data.getBytes()));
            }
            
            col.save(orderObject);
        }
    }
}
