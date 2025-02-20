package com.thundashop.repository.zauiactivityrepository;

import java.util.List;
import java.util.Optional;

import com.thundashop.core.common.DataCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivity;

@org.springframework.stereotype.Repository
public class ZauiActivityRepository extends Repository<ZauiActivity> implements IZauiActivityRepository {

    @Autowired
    public ZauiActivityRepository(@Qualifier("repositoryDatabase") Database database) {
        super(database);
    }

    @Override
    public Optional<ZauiActivity> getById(String id, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("_id", id);
        query.put("className", getClassName());
        return getFirst(query, sessionInfo);
    }

    @Override
    public ZauiActivity getByOptionId(String optionId, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("activityOptionList.id", optionId);
        query.put("deleted", null);
        return getFirst(query, sessionInfo).orElse(null);
    }

    @Override
    public ZauiActivity getBySupplierAndProductId(int supplierId, int productId, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("supplierId", supplierId);
        query.put("productId", productId);
        query.put("deleted", null);
        return getFirst(query, sessionInfo).orElse(null);
    }

    @Override
    public int markDeleted(List<String> zauiActivityIds, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className",getClassName());
        query.put("_id",new BasicDBObject("$in",zauiActivityIds));
        return markDeletedByQuery(query, sessionInfo);
    }

    @Override
    protected String getClassName() {
        return ZauiActivity.class.getName();
    }
}
