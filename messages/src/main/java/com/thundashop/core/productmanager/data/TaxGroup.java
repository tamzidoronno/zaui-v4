package com.thundashop.core.productmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaxGroup extends DataCommon {
    public int groupNumber = 0;
    public int accountingTaxGroupId = -1;
    public Double taxRate = 0.0;
    public String accountingTaxAccount;
    public String description;
    public List<OverrideTaxGroup> overrideTaxGroups = new ArrayList();

    public void addTestGroup() {
        if (!overrideTaxGroups.isEmpty()) {
            return;
        }
    }
    
    
    public Double getTaxRate() {
        return taxRate/100;
    }

    public void addOverrideGroup(Date start, Date end, int overrideGroupNumber) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(start);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        OverrideTaxGroup taxGroup = new OverrideTaxGroup();
        taxGroup.start = cal.getTime();

        cal.setTime(end);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        taxGroup.end = cal.getTime();

        taxGroup.groupNumber = overrideGroupNumber;

        overrideTaxGroups.add(taxGroup);
    }

    
}
