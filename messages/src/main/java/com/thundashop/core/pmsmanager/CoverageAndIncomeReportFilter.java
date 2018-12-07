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
}
