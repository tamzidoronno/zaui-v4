package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PmsAdvancePriceYield extends DataCommon {
    public List<String> types = new ArrayList();
    public Date start;
    public Date end;
    public HashMap<String, YieldEntry> yeilds = new HashMap();
}
