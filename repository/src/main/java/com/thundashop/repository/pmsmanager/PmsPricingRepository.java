package com.thundashop.repository.pmsmanager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.db.Database;

import java.util.List;
import java.util.Optional;

public class PmsPricingRepository extends Repository<PmsPricing> {

    private final String className;

    public PmsPricingRepository(Database database, String dbName, String className) {
        super(database, dbName);
        this.className = className;
    }

    public Optional<PmsPricing> findPmsPricingByCode(String code, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", className);
        query.put("code", code);
        List<PmsPricing> result = getDatabase().query(getDbName(), getCollectionName(sessionInfo), PmsPricing.class, query);
        return getSingle(result, () -> "Found multiple data for: " + PmsPricing.class.getSimpleName() + " code: " + code);
    }

    public String getClassName() {
        return className;
    }
}
