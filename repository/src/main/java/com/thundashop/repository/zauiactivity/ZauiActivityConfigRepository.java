package com.thundashop.repository.zauiactivity;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public class ZauiActivityConfigRepository extends Repository<ZauiActivityConfig> implements IZauiActivityConfigRepository {

    @Autowired
    public ZauiActivityConfigRepository(@Qualifier("repositoryDatabase")Database database) {
        super(database);
    }

    @Override
    public Optional<ZauiActivityConfig> getZauiActivityConfig(SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", ZauiActivityConfig.class.getName());
        return getFirst(query, sessionInfo);
    }
    @Override
    protected String getClassName() {
        return ZauiActivityConfig.class.getName();
    }

    @Override
    public Class<ZauiActivityConfig> getEntityClass() {
        return null;
    }

    @Override
    public List distinct(String field, DBObject query, SessionInfo sessionInfo) {
        return null;
    }
}
