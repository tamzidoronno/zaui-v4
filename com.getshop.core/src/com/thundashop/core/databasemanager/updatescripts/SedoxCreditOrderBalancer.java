/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.sedox.SedoxOrder;
import com.thundashop.core.sedox.SedoxUser;
import java.net.UnknownHostException;
import java.util.Set;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author ktonder
 */
public class SedoxCreditOrderBalancer {
	public static void main(String[] args) throws UnknownHostException {
		
		Morphia morphia = new Morphia();
		morphia.map(DataCommon.class);
		
				
		Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("SedoxProductManager");
        DBCollection collection = db.getCollection("col_608afafe-fd72-4924-aca7-9a8552bc6c81");
		DBCursor cursor = collection.find();
		while(cursor.hasNext()) {
			DBObject dbObject = cursor.next();
			if (dbObject.get("className").equals("com.thundashop.core.sedox.SedoxUser")) {
				DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);
				SedoxUser user = (SedoxUser)dataCommon;
				for (SedoxOrder order : user.orders) {
					if (order.creditAmount < 0) {
						order.creditAmount = order.creditAmount * -1;	
					}
				}
				collection.save(morphia.toDBObject(user));
			}
			
			
		}
	}
}
