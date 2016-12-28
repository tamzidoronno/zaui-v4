package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class PmsOrderStatisticsEntry implements Serializable {
    Date day;
    HashMap<String, Double> priceInc = new HashMap();
    HashMap<String, Double> priceEx = new HashMap();
    HashMap<String, Double> priceIncOnOrder = new HashMap();
    HashMap<Long, Double> orderInc;
    HashMap<Long, Double> orderEx;
}
