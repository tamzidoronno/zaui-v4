package com.thundashop.repository.pmsmanager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public class PmsPricingRepository extends Repository<PmsPricing> implements IPmsPricingRepository {

    @Autowired
    public PmsPricingRepository(@Qualifier("repositoryDatabase")Database database) {
        super(database);
    }

    public Optional<PmsPricing> findPmsPricingByCode(String code, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("code", code);
        query.put("deleted", null);
        return getOne(query, sessionInfo);
    }    

    public int markDeleteByCode(String code, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject().append("className", getClassName()).append("code", code);
        return markDeletedByQuery(query, sessionInfo);
    }

    public List<String> getPriceCodes(SessionInfo sessionInfo) {
        return distinct("code", new BasicDBObject().append("className", getClassName()), sessionInfo);
    }

    public boolean existByCode(String code, SessionInfo sessionInfo) {
        return exist(new BasicDBObject().append("className", getClassName()).append("code", code), sessionInfo);
    }

    @Override
    protected String getClassName() {
        return PmsPricing.class.getName();
    }

    @Override
    protected Class<PmsPricing> getEntityClass() {
        return PmsPricing.class;
    }
}
