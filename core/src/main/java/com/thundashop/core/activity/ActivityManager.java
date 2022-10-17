package com.thundashop.core.activity;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.activity.dto.ActivityConfig;
import com.thundashop.core.activity.dto.OctoProduct;
import com.thundashop.core.activity.service.OctoApiService;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@GetShopSession
public class ActivityManager extends GetShopSessionBeanNamed implements IActivityManager {

    @Autowired
    OctoApiService octoApiService;
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

    @Override
    public List<OctoProduct> getActivities() throws IOException {
        if(activityConfig.getSupplierId() == null) {
            return new ArrayList<>();
        }
        return octoApiService.getProducts(activityConfig.getSupplierId());
    }
}
