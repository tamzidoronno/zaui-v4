package com.thundashop.repository.pmsmanager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.common.Repository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.db.Database;

import java.util.Date;
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
        return getOne(query, PmsPricing.class, sessionInfo);
    }

    public String getClassName() {
        return className;
    }

    public int markDeleteByCode(String code, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject().append("className", className).append("code", code);
        return markDeletedByQuery(query, sessionInfo);
    }
}
