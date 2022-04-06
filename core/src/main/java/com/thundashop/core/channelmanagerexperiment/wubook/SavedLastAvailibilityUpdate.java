package com.thundashop.core.channelmanagerexperiment.wubook;

import com.thundashop.core.common.DataCommon;

import java.util.ArrayList;
import java.util.List;

public class SavedLastAvailibilityUpdate extends DataCommon {
    List<WubookAvailabilityField> lastAvailabilityUpdated = new ArrayList();
    boolean needUpdateMinStay = false;
}
