package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoverageAndIncomeReportFilter {
    public Date start;
    public Date end;
    public List<String> products = new ArrayList();
    public List<String> userIds = new ArrayList();
    public boolean incTaxes = false;
    public String channel = "";
    public List<String> departmentIds = new ArrayList();
    public List<String> orderIds = new ArrayList();
    public List<String> ignoreOrderIds = new ArrayList();
    public List<String> allProducts = new ArrayList();
    public List<String> segments = new ArrayList();

}