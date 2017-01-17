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
        
        // SFI ADM
        if (id.equals("0b3a1ac4-4689-462d-ad06-469e2ffe72dd"))
            return false;
//        
        // SFI UTSTYR
        if (id.equals("6bbba99e-7206-4ce2-800b-a1010fd02b1b"))
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
            Integer a = null;
            Integer b = null;
        
            try {
                String firstOne = o1.name.split(" ")[0];
                firstOne = firstOne.replaceAll("\\D+","");
                a = Integer.parseInt(firstOne);
                
            } catch (Exception ex) {
                
            }
            
            try {
                String firstTwo = o2.name.split(" ")[0];
                firstTwo = firstTwo.replaceAll("\\D+","");
                b = Integer.parseInt(firstTwo);
                
            } catch (Exception ex) {
                
            }
            
            if (a == null && b == null) {
                return -1;
            }
            
            if (a == null ) {
                return 1;
            }
            
            if (b == null ) {
                return -1;
            }
            
            return a.compareTo(b);
        };
    }
}
