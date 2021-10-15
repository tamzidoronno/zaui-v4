package com.thundashop.repository.db;

import com.google.common.collect.ImmutableSet;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.ConferenceData;
import com.thundashop.core.pmsmanager.PmsPricing;

import java.util.Set;

public class EntityMappersImpl implements EntityMappers {

    private final Set<Class> entities;

    public EntityMappersImpl() {
        this.entities = init();
    }

    @Override
    public Set<Class> getEntities() {
        return entities;
    }

    private Set<Class> init() {
        return ImmutableSet.of(
                DataCommon.class,
                ConferenceData.class,
                PmsPricing.class
        );
    }

}
