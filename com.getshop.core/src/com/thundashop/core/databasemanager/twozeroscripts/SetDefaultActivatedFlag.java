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
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SetDefaultActivatedFlag {
	public static void main(String[] args) throws UnknownHostException {
		Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("ApplicationPool");
		for (String name : db.getCollectionNames()) {
			DBCollection col = db.getCollection(name);
			DBCursor cur = col.find();
			while(cur.hasNext()) {
					DBObject ob = cur.next();
				if (ob != null && ob.get("className") != null && ob.get("className").equals("com.thundashop.core.appmanager.data.ApplicationSettings")) {
					
					List<String> defaultActivatedApps = Arrays.asList("Cleanish", "Product", "GoogleMaps", "OrderManager", "ContentManager", "YouTube", "Contact", "Users", "GoogleAnalytics", "ImageDisplayer", "Footer", "Banner", "Search", "MenuEditor");
					
					if (ob.get("type").equals("ThemeApplication") && ob.get("isPublic").equals(true)) {
//						System.out.println("Theme: " + ob.get("appName") + " isPublic" + ob.get("isPublic"));
					}
					if (defaultActivatedApps.contains(ob.get("appName"))) {
						System.out.println("Default activated by startup: " + ob.get("appName"));
						ob.put("defaultActivate", true);
						col.save(ob);						
					}
					

				}
			}
		}
	}
}
