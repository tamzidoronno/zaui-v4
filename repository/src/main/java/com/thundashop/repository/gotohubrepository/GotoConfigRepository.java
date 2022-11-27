package com.thundashop.repository.gotohubrepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.utils.SessionInfo;

@org.springframework.stereotype.Repository
public class GotoConfigRepository extends Repository<GoToConfiguration> implements IGotoConfigRepository {
    @Autowired
    public GotoConfigRepository(@Qualifier("repositoryDatabase") Database database) {
        super(database);
    }

    @Override
    protected String getClassName() {
        return GoToConfiguration.class.getName();
    }

    @Override
    public GoToConfiguration getGotoConfiguration(SessionInfo sessionInfo) throws NotUniqueDataException {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("deleted", null);
        return getOne(query, sessionInfo).orElse(new GoToConfiguration());
    }

}
