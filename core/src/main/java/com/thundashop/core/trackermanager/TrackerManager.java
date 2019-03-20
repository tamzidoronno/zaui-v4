/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackermanager;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.thundashop.core.common.ManagerBase;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class TrackerManager extends ManagerBase implements ITrackerManager  {

    @Override
    public void logTracking(String applicationName, String type, String value, String textDescription) {
//        TrackLog log = new TrackLog();
//        log.sessionId = getSession() != null ? getSession().id : "";
//        log.applicationName = applicationName;
//        log.date = new Date();
//        log.type = type;
//        log.value = value;
//        log.textDescription = textDescription;
//        saveObject(log);
    }

    @Override
    public List<TrackLog> getActivities(Date start, Date end) {
        BasicDBObject query = new BasicDBObject();
        DBObject dateQuery = BasicDBObjectBuilder.start("$gte", start).add("$lte", end).get();
                
        query.put("className", TrackLog.class.getCanonicalName());
        query.put("date", dateQuery);
        
        return database.query("TrackerManager", getStoreId(), query).stream()
                .map(o -> (TrackLog)o)
                .collect(Collectors.toList());
    }

   
    
}
