package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.common.ManagerBase;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@GetShopSession
public class WubookLogManager extends ManagerBase implements IWubookLogManager {

    // TODO: 1. incorporate with repository module. 2. make save method async

    private static final String MANAGER = WubookLogManager.class.getSimpleName();

    @Override
    public void save(String message, Long timeStamp) {
        WubookLog wubookLog = new WubookLog(message, System.currentTimeMillis());
        saveObject(wubookLog);
    }

    @Override
    public Stream<WubookLog> get() {
        long cutOff = getCutOff();

        DBObject searchQuery = new BasicDBObject()
                .append("timeStamp", new BasicDBObject("$gte", cutOff));

        DBObject sortQuery = new BasicDBObject("timeStamp", -1);

        return database.query(MANAGER, storeId, searchQuery, sortQuery, 100)
                .stream()
                .map(i -> (WubookLog) i);
    }

    public static String getManager() {
        return MANAGER;
    }

    public static long getCutOff() {
        return System.currentTimeMillis() - (1000 * 60 * 24 * 3); // 72 minute
    }
}
