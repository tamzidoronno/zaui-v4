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
import com.thundashop.core.sedox.SedoxUserProduct;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author ktonder
 */
public class ConvertSedoxProductToUserProduct {
    public static void main(String[] args) throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("SedoxProductManager");

        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        Set<String> collections = db.getCollectionNames();
        for (String colection : collections) {
            //Now find pages which helds the apps.
            DBCollection selectedCollection = db.getCollection(colection);
            DBCursor allDocs = selectedCollection.find();
            while (allDocs.hasNext()) {
                
                
                DBObject object = allDocs.next();
                String className = (String) object.get("className");
                if (className != null && className.equals("com.thundashop.core.sedox.SedoxProduct")) {
                    SedoxUserProduct product = new SedoxUserProduct();
                    product.comment = (String) object.get("comment");
                    product.isFinished = (Boolean) object.get("isFinished");
                    product.reference = (Map<String, String>) object.get("reference");
                    product.started = (boolean) object.get("started");
                    product.startedByUserId = (String) object.get("startedByUserId");
                    product.states = (Map<String, Date>) object.get("states");
                    product.uploadOrigin = (String) object.get("uploadOrigin");
                    product.useCreditAccount = (String) object.get("useCreditAccount");
                    product.startedDate = (Date) object.get("startedDate");
                    product.uploadedByUserId = (String) object.get("firstUploadedByUserId");
                    product.rowCreatedDate = (Date) object.get("rowCreatedDate");
                    product.id = (String) object.get("_id");
                    System.out.println(product.states);
                    
                    DBObject dbobject = morphia.toDBObject(product);
                    selectedCollection.save(dbobject);
                }
            }
        }
    }
}
