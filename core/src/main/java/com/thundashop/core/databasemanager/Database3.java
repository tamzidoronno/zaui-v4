package com.thundashop.core.databasemanager;

import com.mongodb.*;
import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.db.MongoClientProvider;
import org.mongodb.morphia.Morphia;

import java.util.ArrayList;
import java.util.List;

public class Database3 {
    private final Mongo mongo;

    private final Morphia morphia;

    public Database3(MongoClientProvider provider) {
        mongo = provider.getMongoClient();
        morphia = new Morphia();
        morphia.getMapper().getConverters().addConverter(BigDecimalConverter.class);
        morphia.map(DataCommon.class);
    }

    public List<DataCommon> query(String manager, String storeId, DBObject query, DBObject orderBy, int limit) {
        DBCollection col = getCollection(manager, storeId);
        List<DataCommon> retObjects = new ArrayList<>();

        try (DBCursor res = col.find(query).sort(orderBy).limit(limit)) {
            while (res.hasNext()) {
                DBObject nx = res.next();
                DataCommon data = getMorphia().fromDBObject(DataCommon.class, nx);
                retObjects.add(data);
            }
        }

        return retObjects;
    }

    public DBCollection getCollection(String manager, String storeId1) {
        DB db = getMongo().getDB(manager);
        return db.getCollection("col_" + storeId1);
    }

    public Mongo getMongo() {
        return mongo;
    }

    public Morphia getMorphia() {
        return morphia;
    }

}
