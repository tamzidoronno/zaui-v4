package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.List;

public class SavedOrderFile extends DataCommon {
    public List<String> result;
    public String type = "accounting";
    public boolean transferred = false;
    String subtype = "";
}
