package com.thundashop.repository.bookingrepository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Repository
public class BookingRepository extends Repository<Booking> implements IBookingRepository{

    @Autowired
    public BookingRepository(@Qualifier("repositoryDatabase")Database database) {
        super(database);
    }   

    @Override
    public Booking getById(String id, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("_id", id);
        return getFirst(query, sessionInfo).orElse(null);
    }

    @Override
    protected String getClassName() {
        return Booking.class.getName();
    }
    
}
