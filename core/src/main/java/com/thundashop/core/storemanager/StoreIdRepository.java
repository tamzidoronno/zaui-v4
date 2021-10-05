package com.thundashop.core.storemanager;

import com.mongodb.BasicDBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.Database3;
import com.thundashop.core.storemanager.data.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StoreIdRepository {

    private final Database3 storeIdDb;

    @Autowired
    public StoreIdRepository(@Qualifier("storeIdDb") Database3 storeIdDb) {
        this.storeIdDb = storeIdDb;
    }

    public Integer getNextIncrementalStoreId() {
        List<DataCommon> list = storeIdDb.query("StoreManager",
                "all",
                new BasicDBObject(),
                new BasicDBObject("incrementalStoreId", -1),
                1);

        return (list.isEmpty()) ? 1 : (((Store) list.get(0)).incrementalStoreId) + 1;
    }

}
