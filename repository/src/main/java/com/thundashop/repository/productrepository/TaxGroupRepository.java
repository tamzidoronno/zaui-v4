package com.thundashop.repository.productrepository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Repository
public class TaxGroupRepository extends Repository<TaxGroup> implements ITaxGroupRepository {

    @Autowired
    public TaxGroupRepository(@Qualifier("repositoryDatabase") Database database) {
        super(database);
    }

    @Override
    public TaxGroup getById(String id, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("_id", id);
        return getFirst(query, sessionInfo).orElse(null);
    }

    @Override
    protected String getClassName() {
        return TaxGroup.class.getName();
    }
}
