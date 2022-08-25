package com.thundashop.repository.bookingitemtyperepository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Repository
public class BookingItemTypeRepository extends Repository<BookingItemType> implements IBookingItemTypeRepository{

    @Autowired
    public BookingItemTypeRepository(@Qualifier("repositoryDatabase") Database database) {
        super(database);
    }      

    @Override
    public BookingItemType getById(String id, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("_id", id);
        return getFirst(query, sessionInfo).orElse(null);
    }

    @Override
    protected String getClassName() {       
        return BookingItemType.class.getName();
    }

    @Override
    protected Class<BookingItemType> getEntityClass() {
        return BookingItemType.class;
    }
    
}
