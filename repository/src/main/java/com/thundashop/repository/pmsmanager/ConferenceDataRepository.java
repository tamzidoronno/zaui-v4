package com.thundashop.repository.pmsmanager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.pmsmanager.ConferenceData;
import com.thundashop.repository.common.Repository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.db.Database;

import java.util.List;
import java.util.Optional;

public class ConferenceDataRepository extends Repository<ConferenceData> {

    private final String className;

    public ConferenceDataRepository(Database database, String dbName, String className) {
        super(database, dbName);
        this.className = className;
    }

    public Optional<ConferenceData> findByBookingId(String bookingId, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", className);
        query.put("bookingId", bookingId);
        List<ConferenceData> result = getDatabase().query(getDbName(), getCollectionName(sessionInfo), ConferenceData.class, query);
        return getSingle(result, () -> "Found multiple data for: " + ConferenceData.class.getSimpleName()
                + " bookingId: " + bookingId);
    }

}
