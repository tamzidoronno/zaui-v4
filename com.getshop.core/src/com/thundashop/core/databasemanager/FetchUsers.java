/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import java.net.UnknownHostException;
import java.util.Set;

/**
 *
 * @author boggi
 */
public class FetchUsers {

    public static void main(String args[]) throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("UserManager");
        Set<String> collections = db.getCollectionNames();
        for (String col : collections) {
            DBCursor users = db.getCollection(col).find();
            while (users.hasNext()) {
                DBObject user = users.next();
                String addr = (String) user.get("emailAddress");
                if(addr != null && addr.contains("@")) {
                    System.out.print(addr + ",");
                }
            }
        }
    }
}
