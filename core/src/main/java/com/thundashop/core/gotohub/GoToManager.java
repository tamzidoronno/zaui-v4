package com.thundashop.core.gotohub;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.wubook.WubookLogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@GetShopSession
public class GoToManager extends ManagerBase implements IGoToManager {

    private static final Logger logger = LoggerFactory.getLogger(GoToManager.class);
    private static final String MANAGER = WubookLogManager.class.getSimpleName();
    private GoToSettings settings = new GoToSettings();

    public synchronized void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof GoToSettings) {
                settings = (GoToSettings) dataCommon;
            }
        }
    }

    @Override
    public void changeToken(String newToken) {
        settings.authToken = newToken;

        saveObject(settings);
    }

    @Override
    public String testConnection() throws Exception {
        return settings.authToken;
    }
}
