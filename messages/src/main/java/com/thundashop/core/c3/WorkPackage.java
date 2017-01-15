/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.thundashop.core.common.DataCommon;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class WorkPackage extends DataCommon {
    public String name = "";
    public String description = "";
    public String owner = "";

    boolean shouldRemoveOnePercent(Date end) {
        
        // WP 0
        if (id.equals("a218bccc-ce60-45e2-9f30-f01743d7f8b3"))
            return false;
        
        // WP 1
        if (id.equals("e01597d1-804b-4e63-bf9b-647c4cd4fece"))
            return false;
        
        // WP 11
        if (id.equals("de20c1c3-faee-4237-8457-dc9efed16364"))
            return false;
        
        if (isBefore2017(end)) {
            // WP 3
            if (id.equals("2d9c56ee-c95b-4638-bcb1-93a4f340c931"))
                return false;
            
            // WP 5
            if (id.equals("de20c1c3-faee-4237-8457-dc9efed16364"))
                return false;
        }
        
        return true;
    }

    private boolean isBefore2017(Date end) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        int year = cal.get(Calendar.YEAR);
        if (year < 2017)
            return true;
        
        return false;
        
    }
    
    static Comparator<? super WorkPackage> getComperator() {
        return (WorkPackage o1, WorkPackage o2) -> {
            try {
                String firstOne = o1.name.split(" ")[0];
                String firstTwo = o2.name.split(" ")[0];
                
                firstOne = firstOne.replaceAll("\\D+","");
                firstTwo = firstTwo.replaceAll("\\D+","");
                
                Integer a = Integer.parseInt(firstOne);
                Integer b = Integer.parseInt(firstTwo);
                return a.compareTo(b);
            } catch (Exception ex) {
                return 0;
            }
        };
    }
}
