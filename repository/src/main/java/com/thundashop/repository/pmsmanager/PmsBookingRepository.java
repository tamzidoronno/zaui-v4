package com.thundashop.repository.pmsmanager;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Repository
public class PmsBookingRepository extends Repository<PmsBooking> implements IPmsBookingRepository{

    private final Map<String, String> q;

    @Autowired
    public PmsBookingRepository(@Qualifier("repositoryDatabase")Database database) {
        super(database);
        q = ImmutableMap.of("className", getClassName());
    }

    public Optional<PmsBooking> findBySessionId(String sessionId, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject(q).append("deleted", null).append("sessionId", sessionId);
        return  getFirst(query, sessionInfo);
    }

    public Optional<PmsBooking> findByPmsBookingRoomId(String pmsBookingRoomId, SessionInfo sessionInfo) {
        return null;
    }

    @Override
    protected String getClassName() {       
        return PmsBooking.class.getName();
    }

    @Override
    protected Class<PmsBooking> getEntityClass() {
        return PmsBooking.class;
    }

}
