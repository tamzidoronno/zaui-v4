package com.thundashop.repository.gotohubrepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;

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
    
}
