package com.thundashop.zauiactivity.dto;

import java.util.ArrayList;
import java.util.List;

import com.thundashop.core.common.DataCommon;


public class ZauiActivityConfig extends DataCommon {
    public boolean enabled;
    public List<ZauiConnectedSupplier> connectedSuppliers = new ArrayList<>();
    public int resellerId;

    public void setActivityConfig(ZauiActivityConfig activityConfig) {
        enabled = activityConfig.enabled;
        connectedSuppliers = activityConfig.connectedSuppliers;
        resellerId = activityConfig.resellerId;
    }
}
