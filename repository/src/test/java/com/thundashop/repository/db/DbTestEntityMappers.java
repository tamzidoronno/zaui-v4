package com.thundashop.repository.db;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class DbTestEntityMappers implements EntityMappers {

    @Override
    public Set<Class> getEntities() {
        return ImmutableSet.of(DbTest.class);
    }

}
