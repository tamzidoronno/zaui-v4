package com.thundashop.core.channelmanagerexperiment.wubook;

import com.thundashop.core.common.DataCommon;

import java.util.Date;
import java.util.List;

public class WubookAvailabilityRestrictions extends DataCommon {
    public Date start;
    public Date end;
    public List<String> types;

    public Date getStartDate() {
        return start;
    }
}
