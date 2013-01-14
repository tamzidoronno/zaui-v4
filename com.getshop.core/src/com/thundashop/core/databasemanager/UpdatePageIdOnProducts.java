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
import com.thundashop.core.productmanager.data.Product;
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
        DB pageManagerDb = m.getDB("PageManager");
        Set<String> collections = db.getCollectionNames();
        for(String col : collections) {
            DBCursor productQuery = db.getCollection(col).find();
            while(productQuery.hasNext()) {
                DBObject product = productQuery.next();
                if(product.get("page") != null) {
                    BasicDBObject productPage = (BasicDBObject) product.get("page");
                    String pageId = (String) productPage.get("_id");
                    product.put("pageId", pageId);
                    db.getCollection(col).save(product);
                    
                    BasicDBObject productParentPage = (BasicDBObject) productPage.get("parent");
                    if(productParentPage != null) {
                        String productParentPageId = (String) productParentPage.get("_id");
                                
                        DBObject dbObj = new BasicDBObject();
                        dbObj.put("_id", pageId);
                        DBCursor found = pageManagerDb.getCollection(col).find(dbObj);

                        //Check if they are in sync.
                        BasicDBObject pageManagerPage = (BasicDBObject) found.next();
                        BasicDBObject pageManagerPageParent = (BasicDBObject)pageManagerPage.get("parent");
                        if(pageManagerPageParent != null) {
                            String pageManagerPageParentId = (String) pageManagerPageParent.get("_id");
                            if(!pageManagerPageParentId.equals(productParentPageId)) {
                                System.out.println("Page does not equals");
                                pageManagerPage.put("parent", productParentPage);
                                pageManagerDb.getCollection(col).save(pageManagerPage);
                            }
                        } else if(productParentPageId != null) {
                            System.out.println("Product parent page should exists");
                            pageManagerPage.put("parent", productParentPage);
                            pageManagerDb.getCollection(col).save(pageManagerPage);
                        }
                    }
                }
            }
        }
    }
}
