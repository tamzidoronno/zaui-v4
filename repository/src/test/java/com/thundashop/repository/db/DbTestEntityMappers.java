package com.thundashop.repository.db;

import com.google.common.collect.ImmutableSet;
import com.thundashop.repository.entitymapper.IEntityMapper;

import java.util.Set;

public class DbTestEntityMappers implements IEntityMapper {

    @Override
    public Set<Class> getEntities() {
        return ImmutableSet.of(DbTest.class);
    }

}
