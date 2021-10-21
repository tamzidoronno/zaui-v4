package com.thundashop.core.pmsmanager;

import com.mongodb.BasicDBObject;

import java.util.List;

public interface IPmsLogManager {

    void save(PmsLog pmsLog);

    List<PmsLog> query(BasicDBObject query, BasicDBObject sort, int limit);

}
