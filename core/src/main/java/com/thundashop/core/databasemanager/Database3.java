package com.thundashop.core.databasemanager;

import com.mongodb.*;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopLogHandler;
import org.mongodb.morphia.Morphia;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Database3 {
    private final Mongo mongo;

    private final Morphia morphia;

    public Database3(String host, int port) {
        try {
            mongo = new Mongo(host, port);
        } catch (UnknownHostException e) {
            GetShopLogHandler.logStack(e, null);
            throw new RuntimeException(e);
        }
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
                DataCommon data = morphia.fromDBObject(DataCommon.class, nx);
                retObjects.add(data);
            }
        }

        return retObjects;
    }

    public DBCollection getCollection(String manager, String storeId1) {
        DB db = mongo.getDB(manager);
        return db.getCollection("col_" + storeId1);
    }
}
