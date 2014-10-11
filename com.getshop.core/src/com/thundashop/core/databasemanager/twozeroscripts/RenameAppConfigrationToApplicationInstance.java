/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.twozeroscripts;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import java.net.UnknownHostException;

/**
 *
 * @author ktonder
 */
public class RenameAppConfigrationToApplicationInstance {
	public static void main(String[] args) throws UnknownHostException {
		Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("PageManager");
		for (String name : db.getCollectionNames()) {
			DBCollection col = db.getCollection(name);
			DBCursor cur = col.find();
			while(cur.hasNext()) {
				DBObject ob = cur.next();
				if (ob != null && ob.get("className") != null && ob.get("className").equals("com.thundashop.core.common.AppConfiguration")) {
					ob.put("className", "com.thundashop.core.common.ApplicationInstance");
					col.save(ob);
				}
			}
		}
	}
}
