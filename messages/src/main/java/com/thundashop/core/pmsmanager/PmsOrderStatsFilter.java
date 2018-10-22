package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsOrderStatsFilter extends DataCommon {
    public String name = "";
    public Date start;
    public Date end;
    List<PmsPaymentMethods> methods = new ArrayList();
    public String displayType;
    public String priceType;
    public boolean includeVirtual = false;
    public boolean fromPmsModule = false;
    public Integer shiftHours = 0;
    
    public String savedPaymentMethod = "";
    public String channel;
}
