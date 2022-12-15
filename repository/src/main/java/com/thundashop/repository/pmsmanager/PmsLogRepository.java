package com.thundashop.repository.pmsmanager;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mongodb.BasicDBObject;
import com.thundashop.core.pmsmanager.PmsLog;
import com.thundashop.repository.baserepository.Repository;
import com.thundashop.repository.db.Database;
import com.thundashop.repository.utils.SessionInfo;

@org.springframework.stereotype.Repository
public class PmsLogRepository extends Repository<PmsLog> implements IPmsLogRepository {

    @Autowired
    public PmsLogRepository(@Qualifier("repositoryDatabase")Database database) {
        super(database);
    }

    public List<PmsLog> query(PmsLog filter, SessionInfo sessionInfo, Date start, Date end) {
        BasicDBObject query = new BasicDBObject();
        BasicDBObject sort = new BasicDBObject();

        query.put("className", getClassName());
        if (isNotEmpty(filter.bookingId)) {
            query.put("bookingId", filter.bookingId);
        }
        if (isNotEmpty(filter.logType)) {
            query.put("logType", filter.logType);
        }
        if (isNotEmpty(filter.tag)) {
            query.put("tag", filter.tag);
        }
        if (isNotEmpty(filter.bookingItemId)) {
            query.put("bookingItemId", filter.bookingItemId);
        }
        if (isNotEmpty(filter.roomId)) {
            query.put("roomId", filter.roomId);
        }

        BasicDBObject dateFilter = new BasicDBObject();
        if (start != null) {
            dateFilter.append("$gte", start);
        }
        if (end != null) {
            dateFilter.append("$lte", end);
        }
        if(dateFilter.size() > 0){
            query.put("dateEntry", dateFilter);
        }
        sort.put("rowCreatedDate", -1);
        int limit = filter.includeAll ? Integer.MAX_VALUE : 100;

        return getDatabase().query(sessionInfo.getManagerName(), getCollectionName(sessionInfo), query, sort, limit);
    }

    @Override
    protected String getClassName() {
        return PmsLog.class.getName();
    }

}
