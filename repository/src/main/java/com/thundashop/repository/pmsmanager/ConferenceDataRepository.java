package com.thundashop.repository.pmsmanager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.repository.utils.SessionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.thundashop.core.pmsmanager.ConferenceData;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;

import java.util.Optional;

@org.springframework.stereotype.Repository
public class ConferenceDataRepository extends Repository<ConferenceData> implements IConferenceDataRepository{

    @Autowired
    public ConferenceDataRepository(@Qualifier("repositoryDatabase")Database database) {
        super(database);
    }

    public Optional<ConferenceData> findByBookingId(String bookingId, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("bookingId", bookingId);
        return getFirst(query, sessionInfo);
    }

    @Override
    protected String getClassName() {        
        return ConferenceData.class.getName();
    }

}
