package com.thundashop.repository.pmsmanager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.common.SessionInfo;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.exceptions.NotUniqueDataException;

import java.util.List;
import java.util.Optional;

public class PmsPricingRepository extends Repository {

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

        if (result.size() > 1) {
            throw new NotUniqueDataException("Found multiple data for: " + PmsPricing.class.getSimpleName() + " code: " + code);
        }

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public String getClassName() {
        return className;
    }
}
