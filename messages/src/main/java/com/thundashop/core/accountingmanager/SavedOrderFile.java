package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class SavedOrderFile extends DataCommon {
    public List<String> result;
    public String type = "accounting";
    public boolean transferred = false;
    public String subtype = ""; 
    public Double amountEx = 0.0;
    public Double amountInc = 0.0;
    public Double amountExDebet = 0.0;
    public Double amountIncDebet = 0.0;
    public Double sumAmountExOrderLines = 0.0;
    public Double sumAmountIncOrderLines = 0.0;
    public List<String> orders = new ArrayList();
}
