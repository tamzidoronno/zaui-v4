package com.thundashop.zauiactivity.dto;

import java.util.ArrayList;
import java.util.List;

import com.thundashop.core.common.DataCommon;
import lombok.Getter;


@Getter
public class ZauiActivityConfig extends DataCommon {
    private boolean enabled;
    private List<ZauiConnectedSupplier> connectedSuppliers = new ArrayList<>();
    private int resellerId;

    public void setActivityConfig(ZauiActivityConfig activityConfig) {
        enabled = activityConfig.enabled;
        connectedSuppliers = activityConfig.connectedSuppliers;
        resellerId = activityConfig.resellerId;
    }
}
