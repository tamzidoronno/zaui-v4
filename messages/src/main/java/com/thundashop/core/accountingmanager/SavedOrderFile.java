package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

public class SavedOrderFile extends DataCommon {
    public List<String> result;
    public String type = "accounting";
    public boolean transferred = false;
    public Date startDate;
    public Date endDate;
    public String subtype = ""; 
    public Double amountEx = 0.0;
    public Double amountInc = 0.0;
    public Double amountExDebet = 0.0;
    public Double amountIncDebet = 0.0;
    public Double sumAmountExOrderLines = 0.0;
    public Double sumAmountIncOrderLines = 0.0;
    public HashMap<String, Double> amountOnOrder = new HashMap();
    public List<String> tamperedOrders = new ArrayList();
    public List<String> orders = new ArrayList();
    public String configId = "";
    public String transferId;
    
    @Transient
    public Integer numberOfOrdersNow = 0;
}
