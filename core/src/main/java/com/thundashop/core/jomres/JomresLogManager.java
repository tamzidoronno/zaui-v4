package com.thundashop.core.jomres;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.common.ManagerBase;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@GetShopSession
public class JomresLogManager extends ManagerBase implements IJomresLogManager {

    // TODO: 1. incorporate with repository module. 2. make save method async

    private static final String MANAGER = JomresLogManager.class.getSimpleName();

    @Override
    public void save(String message, Long timeStamp) {
        JomresLog jomresLog = new JomresLog(message, System.currentTimeMillis());

        saveObject(jomresLog);
    }

    @Override
    public Stream<JomresLog> get() {
        long cutOff = getCutOff();

        DBObject searchQuery = new BasicDBObject()
                .append("timeStamp", new BasicDBObject("$gte", cutOff));

        DBObject sortQuery = new BasicDBObject("timeStamp", -1);

        return database.query(MANAGER, storeId, searchQuery, sortQuery, 2_000)
                .stream()
                .map(i -> (JomresLog) i);
    }

    public static String getManager() {
        return MANAGER;
    }

    public static long getCutOff() {
        return System.currentTimeMillis() - (1000 * 60 * 24 * 3); // 72 minute
    }
}
