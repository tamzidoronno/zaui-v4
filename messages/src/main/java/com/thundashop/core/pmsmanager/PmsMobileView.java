package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PmsMobileView implements Serializable {
    static class PmsMobileViewType {
        public static Integer DAILY = 1;
        public static Integer ALLACTIVE = 2;
    }
    public String id = UUID.randomUUID().toString();
    public String name = "";
    public String icon = "";
    public Integer viewType = 1;
    public Integer daysDisplacement = 0;
    public List<String> products = new ArrayList();
    public boolean paidFor = false;
}
