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
import com.thundashop.core.sedox.SedoxBinaryFile;
import com.thundashop.core.sedox.SedoxSharedProduct;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
                    SedoxSharedProduct product = new SedoxSharedProduct();
                    product.id = UUID.randomUUID().toString();
                    
                    product.binaryFiles = (List<SedoxBinaryFile>) object.get("binaryFiles"); 
                    product.filedesc =  (String) object.get("filedesc"); 
                    product.brand =  (String) object.get("brand"); 
                    product.model =  (String) object.get("model"); 
                    product.engineSize =  (String) object.get("engineSize"); 
                    product.year =  (String) object.get("year"); 
                    product.power = (String) object.get("power"); 
                    product.ecuType =  (String) object.get("ecuType"); 
                    product.build =  (String) object.get("build"); 
                    product.storeId = "608afafe-fd72-4924-aca7-9a8552bc6c81";
                    product.ecuBrand = (String) object.get("ecuBrand"); 
                    product.softwareNumber = (String) object.get("softwareNumber"); 
                    product.softwareSize =  (String) object.get("softwareSize"); 
                    product.tool =  (String) object.get("tool"); 
                    product.status =  (String) object.get("status"); 
                    product.originalChecksum = (String) object.get("originalChecksum"); 
                    product.gearType = (String) object.get("gearType"); 
                    
                    if (object.get("isCmdEncryptedProduct") != null) {
                        product.isCmdEncryptedProduct =  (boolean) object.get("isCmdEncryptedProduct"); 
                    }
                    product.channel = (String) object.get("channel"); 
                    product.ecuPartNumber = (String) object.get("ecuPartNumber"); 
                    product.ecuHardwareNumber = (String) object.get("ecuHardwareNumber"); 
                    product.ecuSoftwareNumber = (String) object.get("ecuSoftwareNumber"); 
                    product.ecuSoftwareVersion = (String) object.get("ecuSoftwareVersion"); 
                    if (object.get("saleAble") != null) 
                        product.saleAble = (boolean) object.get("saleAble"); 
                    product.rowCreatedDate =  (Date) object.get("rowCreatedDate"); 
                    
                    
                    object.put("sharedProductId", product.id);
                    
                    DBObject dbobject = morphia.toDBObject(product);
                    selectedCollection.save(dbobject);
                    selectedCollection.save(object);
                }
            }
        }
    }
}
