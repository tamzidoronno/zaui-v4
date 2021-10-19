package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.thundashop.core.common.ManagerBase;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@GetShopSession
public class PmsLogManager extends ManagerBase implements IPmsLogManager {

    private static final String MANAGER = PmsLogManager.class.getSimpleName();


    @Override
    public void save(PmsLog pmsLog) {
        super.saveObject(pmsLog);
    }

    @Override
    public List<PmsLog> query(BasicDBObject query, BasicDBObject sort, int limit) {
        return database.query(MANAGER, storeId, query, sort, limit)
                .stream()
                .map(i -> (PmsLog) i)
                .collect(toList());
    }

}
