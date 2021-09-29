package com.thundashop.repository.pmsmanager;

import com.mongodb.BasicDBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.repository.common.SessionInfo;
import com.thundashop.repository.db.Database;
import com.thundashop.core.pmsmanager.PmsLog;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class PmsLogRepository {

    private final Database database;

    private final String dbName;

    public PmsLogRepository(Database database, String dbName) {
        this.database = database;
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public PmsLog save(PmsLog pmsLog, SessionInfo sessionInfo) {
        if (isEmpty(pmsLog.id)) {
            pmsLog.id = UUID.randomUUID().toString();
        }

        if (pmsLog.rowCreatedDate == null) {
            pmsLog.rowCreatedDate = new Date();
        }

        if (isNotEmpty(sessionInfo.getCurrentUserId())) {
            pmsLog.lastModifiedByUserId = sessionInfo.getCurrentUserId();
        }

        pmsLog.storeId = sessionInfo.getStoreId();
        pmsLog.lastModified = new Date();

        if (isNotEmpty(sessionInfo.getLanguage())) {
            String lang = sessionInfo.getLanguage();
            pmsLog.validateTranslationMatrix();
            pmsLog.updateTranslation(lang);
        }

        DataCommon dataCommon = database.save(dbName, "col_" + sessionInfo.getStoreId(), pmsLog);
        return (PmsLog) dataCommon;
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

        return database.query(dbName, "col_" + sessionInfo.getStoreId(), query, sort, limit)
                .stream()
                .map(i -> (PmsLog) i)
                .collect(toList());
    }

}
