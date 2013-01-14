/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.pagemanager.data.Page;
import java.net.UnknownHostException;
import java.util.Set;

/**
 *
 * @author boggi
 */
public class UpdatePageIdOnProducts {
    
     public static void main(String args[]) throws UnknownHostException, ErrorException {
        Mongo m = new Mongo( "localhost" , 27017);
        DB db = m.getDB("ProductManager");
        Set<String> collections = db.getCollectionNames();
        for(String col : collections) {
            DBCursor users = db.getCollection(col).find();
            while(users.hasNext()) {
                DBObject page = users.next();
                if(page.get("page") != null) {
                    BasicDBObject pageObj = (BasicDBObject) page.get("page");
                    String pageId = (String) pageObj.get("_id");
                    System.out.println(pageId);
                    page.put("pageId", pageId);
                    db.getCollection(col).save(page);
                }
            }
        }
    }
}
