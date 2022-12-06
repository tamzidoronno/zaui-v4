package com.thundashop.zauiactivity.dto;

import com.thundashop.core.common.DataCommon;
import lombok.Data;

import java.util.List;

@Data
public class ZauiActivityConfig extends DataCommon {
    boolean enabled;
    List<Integer> supplierIds;
    Integer resellerId;

    public void setActivityConfig(ZauiActivityConfig activityConfig) {
        enabled = activityConfig.enabled;
        supplierIds = activityConfig.supplierIds;
        resellerId = activityConfig.resellerId;
    }
}
