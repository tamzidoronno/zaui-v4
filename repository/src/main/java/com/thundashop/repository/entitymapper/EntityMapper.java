package com.thundashop.repository.entitymapper;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.ConferenceData;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsPricing;

import org.springframework.stereotype.Component;

@Component
public class EntityMapper implements IEntityMapper {

    private final Set<Class> entities;

    public EntityMapper() {
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
                PmsPricing.class,
                PmsBooking.class,
                BookingItemType.class,
                BookingItemType.class,
                Booking.class
        );
    }

}
