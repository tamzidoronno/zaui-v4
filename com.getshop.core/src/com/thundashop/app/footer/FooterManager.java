package com.thundashop.app.footer;

import com.getshop.scope.GetShopSession;
import com.thundashop.app.footermanager.data.Configuration;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class FooterManager extends ManagerBase implements IFooterManager {

    public Configuration configObject;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dbobj : data.data) {
            if (dbobj instanceof Configuration) {
                Configuration confobj = (Configuration)dbobj;
                if(confobj.columnIds == null) {
                    try {
                        databaseSaver.deleteObject(confobj, credentials);
                    }catch(ErrorException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                configObject = (Configuration) dbobj;
            }
        }
    }

    @Override
    public Configuration setLayout(Integer numberOfColumns) throws ErrorException {
        Configuration config = getConfiguration();
        config.numberOfColumns = numberOfColumns;
        databaseSaver.saveObject(config, credentials);

        return config;
    }

    @Override
    public Configuration getConfiguration() throws ErrorException {
        if (configObject == null) {
            this.configObject = new Configuration();
            this.configObject.columnType = new HashMap();
            this.configObject.columnIds = new HashMap();
            for (int i = 0; i < 4; i++) {
                this.configObject.columnType.put(i, 0);
                this.configObject.columnIds.put(i, UUID.randomUUID().toString());
            }
            this.configObject.storeId = storeId;
            databaseSaver.saveObject(this.configObject, credentials);
        }
        return configObject;
    }

    @Override
    public Configuration setType(Integer column, Integer type) throws ErrorException {
        Configuration config = getConfiguration();
        config.columnType.put(column, type);
        databaseSaver.saveObject(config, credentials);
        return config;
    }
}
