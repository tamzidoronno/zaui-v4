package com.thundashop.repository.pmsmanager;

import com.mongodb.BasicDBObject;
import com.thundashop.core.pmsmanager.PmsLog;
import com.thundashop.repository.common.Repository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.db.Database;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class PmsLogRepository extends Repository<PmsLog> {

    public PmsLogRepository(Database database, String dbName) {
        super(database, dbName);
    }

    public List<PmsLog> query(PmsLog filter, SessionInfo sessionInfo) {
        BasicDBObject query = new BasicDBObject();
        BasicDBObject sort = new BasicDBObject();

        query.put("className", PmsLog.class.getCanonicalName());
        if (isNotEmpty(filter.bookingId)) {
            query.put("bookingId", filter.bookingId);
        }
        if (isNotEmpty(filter.logType)) {
            query.put("logType", filter.logType);
        }
        if (isNotEmpty(filter.tag)) {
            query.put("tag", filter.tag);
        }
        if (isNotEmpty(filter.bookingItemId)) {
            query.put("bookingItemId", filter.bookingItemId);
        }
        if (isNotEmpty(filter.roomId)) {
            query.put("roomId", filter.roomId);
        }

        sort.put("rowCreatedDate", -1);
        int limit = filter.includeAll ? Integer.MAX_VALUE : 100;

        return getDatabase().query(getDbName(), getCollectionName(sessionInfo), PmsLog.class, query, sort, limit);
    }

}
