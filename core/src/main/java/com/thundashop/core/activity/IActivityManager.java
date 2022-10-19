package com.thundashop.core.activity;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.activity.dto.ActivityConfig;
import com.thundashop.core.activity.dto.OctoProduct;
import com.thundashop.core.common.GetShopApi;

import java.io.IOException;
import java.util.List;

@GetShopApi
@GetShopSession
public interface IActivityManager {
    ActivityConfig getActivityConfig();

    void updateActivityConfig(ActivityConfig newActivityConfig);

    List<OctoProduct> getActivities() throws IOException;

    String getSupplierName();
}
