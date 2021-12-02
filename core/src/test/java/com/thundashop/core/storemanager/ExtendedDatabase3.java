package com.thundashop.core.storemanager;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.Database3;
import com.thundashop.repository.db.MongoClientProvider;

public class ExtendedDatabase3 extends Database3 {

    public ExtendedDatabase3(MongoClientProvider provider) {
        super(provider);
    }

    public void save(String manager, String storeId, DataCommon data) {
        DBCollection col = getCollection(manager, storeId);
        DBObject dbObject = getMorphia().toDBObject(data);
        col.save(dbObject);
    }

    public void dropDatabase(String manager) {
        getMongo().dropDatabase(manager);
    }
}
