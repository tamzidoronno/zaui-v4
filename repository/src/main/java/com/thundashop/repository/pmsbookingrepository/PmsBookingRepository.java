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
    public List<PmsBooking> getGotoBookings(SessionInfo sessionInfo) {
       DBObject query = new BasicDBObject();
       query.put("className", getClassName());
       query.put("channel", GotoConstants.GOTO_BOOKING_CHANNEL_NAME);
       query.put("deleted", null);
        return getDatabase().query(sessionInfo.getManagerName(), getCollectionName(sessionInfo), query);
    }
    
}
