package com.thundashop.repository.common;

import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.db.DbTest;

public class RepositoryTestImpl extends Repository<DbTest> {

    public RepositoryTestImpl(Database database) {
        super(database);
    }

    @Override
    protected String getClassName() {        
        return DbTest.class.getName();
    }
}
