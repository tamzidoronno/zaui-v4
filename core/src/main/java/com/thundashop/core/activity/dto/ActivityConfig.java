package com.thundashop.core.activity.dto;

import com.thundashop.core.common.DataCommon;

public class ActivityConfig extends DataCommon {
    boolean enabled;
    Integer supplierId;
    Integer resellerId;

    public void setActivityConfig(ActivityConfig activityConfig) {
        enabled = activityConfig.enabled;
        supplierId = activityConfig.supplierId;
        resellerId = activityConfig.resellerId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }
}
