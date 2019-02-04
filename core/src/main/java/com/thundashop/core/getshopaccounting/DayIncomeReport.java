/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class DayIncomeReport extends DataCommon {
    public Date start;
    public Date end;
    public List<DayIncome> incomes = new ArrayList();
    public String createdBy = "";
    
    public void replaceDayIncomes(List<DayIncome> newIncomes) {
        List<DayIncome> toReplace = new ArrayList();
        for (DayIncome oldIncomce : incomes) {
            DayIncome toBeReplaced = getReplacement(newIncomes, oldIncomce);
            toReplace.add(toBeReplaced);
        }
        incomes = toReplace;
    }

    private DayIncome getReplacement(List<DayIncome> newIncomes, DayIncome oldIncomce) {
        for (DayIncome o : newIncomes) {
            if (o.start.equals(oldIncomce.start) && o.end.equals(oldIncomce.end))
                return o;
        }
        
        throw new NullPointerException("Did not find a replacement for the day");
    }
}
