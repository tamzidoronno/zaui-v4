package com.thundashop.repository.pmsbookingrepository;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.gotohub.constant.GotoConstants;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;

@org.springframework.stereotype.Repository
public class PmsBookingRepository extends Repository<PmsBooking> implements IPmsBookingRepository{

    public PmsBookingRepository(Database database) {
        super(database);
    }

    @Override
    protected String getClassName() {
        return PmsBooking.class.getName();
    }

    @Override
    public PmsBooking getPmsBookingById(String id, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("_id", id);
        query.put("className", getClassName());
        query.put("deleted", null);
        return getFirst(query, sessionInfo).orElse(null);
    }

    @Override
    public List<PmsBooking> getGotoBookings(SessionInfo sessionInfo) {
       DBObject query = new BasicDBObject();
       query.put("className", getClassName());
       query.put("channel", GotoConstants.GOTO_BOOKING_CHANNEL_NAME);
       query.put("deleted", null);
        return getDatabase().query(sessionInfo.getManagerName(), getCollectionName(sessionInfo), query);
    }

    @Override
    public PmsBooking getPmsBookingByAddonId(String addonId, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("bookingZauiActivityItems.addonId", addonId);
        query.put("deleted", null);
        return getFirst(query, sessionInfo).orElse(null);
    }
    @Override
    public PmsBooking getPmsBookingByZauiActivityItemId(String activityItemId, SessionInfo sessionInfo) {
        DBObject query = new BasicDBObject();
        query.put("className", getClassName());
        query.put("bookingZauiActivityItems.id", activityItemId);
        query.put("deleted", null);
        return getFirst(query, sessionInfo).orElse(null);
    }
    
}
