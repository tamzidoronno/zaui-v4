/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageArea;
import com.thundashop.core.pagemanager.data.RowLayout;
import java.net.UnknownHostException;
import java.util.Set;

/**
 *
 * @author ktonder
 */
public class MovePageAreas {
    public static void main(String args[]) throws UnknownHostException {
        Mongo m = new Mongo("localhost", 27017);
        DB db = m.getDB("PageManager");
        Set<String> collectionNames = db.getCollectionNames();
        for (String collectionName : collectionNames) {
            DBCollection collection = db.getCollection(collectionName);
            DBCursor stores = collection.find();
            while (stores.hasNext()) {
                DBObject data = stores.next();
                Morphia morphia = new Morphia();
                morphia.map(DataCommon.class);
                DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, data);
            
                if (dataCommon instanceof Page) {
                    Page page = (Page)dataCommon;
                    int j = 1;
                    for (RowLayout rowLayout : page.layout.rows) {
//                        for (int i=0; i<rowLayout.numberOfCells; i++) {
//                            String pageAreaName = "col_"+j;
//                            PageArea pageArea = page.getPageAreaWithNullResult(pageAreaName);
//                            if (pageArea != null) {
//                                System.out.println(pageArea);
//                                page.removePageArea(pageAreaName);
//                                rowLayout.cells.add(pageArea);
//                            }
//                            j++;
//                        }
                    }
                    
                    DBObject dbObject = morphia.toDBObject(page);
                    collection.save(dbObject);
                }
            }
        }
    }
}
