package com.thundashop.repository.zauiactivityrepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

@org.springframework.stereotype.Repository
public class ZauiActivityConfigRepository extends Repository<ZauiActivityConfig>
        implements IZauiActivityConfigRepository {

    @Autowired
    public ZauiActivityConfigRepository(@Qualifier("repositoryDatabase") Database database) {
        super(database);
    }

    @Override
    public Optional<ZauiActivityConfig> getZauiActivityConfig(SessionInfo sessionInfo) throws NotUniqueDataException {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("deleted", null);
        return getOne(query, sessionInfo);
    }

    @Override
    protected String getClassName() {
        return ZauiActivityConfig.class.getName();
    }
}
