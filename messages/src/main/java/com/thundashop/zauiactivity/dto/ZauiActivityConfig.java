package com.thundashop.zauiactivity.dto;

import com.thundashop.core.common.DataCommon;

import java.util.List;

public class ZauiActivityConfig extends DataCommon {
    boolean enabled;
    List<String> supplierIds;
    Integer resellerId;

    public void setActivityConfig(ZauiActivityConfig activityConfig) {
        enabled = activityConfig.enabled;
        supplierIds = activityConfig.supplierIds;
        resellerId = activityConfig.resellerId;
    }
}
