package com.thundashop.repository.bookingitemrepository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@org.springframework.stereotype.Repository
public class BookingItemRepository extends Repository<BookingItem> implements IBookingItemRepository{

    @Autowired
    public BookingItemRepository(@Qualifier("repositoryDatabase")Database database) {
        super(database);
    }    

    @Override
    public BookingItem getById(String id, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("_id", id);
        return getFirst(query, sessionInfo).orElse(null);
    }

    @Override
    protected String getClassName() {        
        return BookingItem.class.getName();
    }
    
}
