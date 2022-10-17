package com.thundashop.core.activity.dto;

import com.thundashop.core.common.DataCommon;

public class ActivityConfig extends DataCommon {
    Integer supplierId;
    Integer resellerId;

    public void setActivityConfig(ActivityConfig activityConfig) {
        supplierId = activityConfig.supplierId;
        resellerId = activityConfig.resellerId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }
}
