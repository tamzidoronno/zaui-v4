package com.thundashop.core.activity;

import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.activity.dto.ActivityConfig;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;

public class ActivityManager extends GetShopSessionBeanNamed implements IActivityManager {
    ActivityConfig activityConfig = new ActivityConfig();

    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof ActivityConfig) {
                activityConfig = (ActivityConfig) dataCommon;
            }
        }
    }
    @Override
    public ActivityConfig getActivityConfig() {
        return activityConfig;
    }
    @Override
    public void updateActivityConfig(ActivityConfig newActivityConfig) {
        activityConfig.setActivityConfig(newActivityConfig);
        saveObject(activityConfig);
    }
}
