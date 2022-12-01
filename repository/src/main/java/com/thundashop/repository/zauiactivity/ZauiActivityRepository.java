package com.thundashop.repository.zauiactivity;

import java.util.List;
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
    public Optional<ZauiActivity> getZauiActivity(SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", ZauiActivity.class.getName());
        return getFirst(query, sessionInfo);
    }

    @Override
    protected String getClassName() {
        return ZauiActivity.class.getName();
    }

    @Override
    public List distinct(String field, DBObject query, SessionInfo sessionInfo) {
        return null;
    }
}
