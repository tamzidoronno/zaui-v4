package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.HashMap;

public class CleaningStatistics implements Serializable {
    public String typeId = "";
    public HashMap<Integer, Double> cleanings = new HashMap();
    public Integer itemCount = 0;
}
