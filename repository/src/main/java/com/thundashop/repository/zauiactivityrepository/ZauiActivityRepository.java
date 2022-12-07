package com.thundashop.repository.zauiactivityrepository;

import java.util.Optional;

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
        query.put("deleted", null);
        return getFirst(query, sessionInfo);
    }

    @Override
    protected String getClassName() {
        return ZauiActivity.class.getName();
    }
}
