package com.thundashop.zauiactivity.dto;

import com.thundashop.core.common.DataCommon;

public class ZauiActivityConfig extends DataCommon {
    boolean enabled;
    Integer supplierId;
    Integer resellerId;

    public void setActivityConfig(ZauiActivityConfig activityConfig) {
        enabled = activityConfig.enabled;
        supplierId = activityConfig.supplierId;
        resellerId = activityConfig.resellerId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }
}
