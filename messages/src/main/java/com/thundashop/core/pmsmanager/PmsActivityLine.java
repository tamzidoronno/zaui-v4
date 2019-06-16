/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.HashMap;
import java.util.List;

public class PmsActivityLine {
    String type = "";
    HashMap<String, PmsActivityEntry> entry = new HashMap();
    
    public boolean canAdd(List<PmsActivityEntry> entries) {
        for(PmsActivityEntry tmp : entries) {
            if(entry.containsKey(tmp.date)) {
                return false;
            }
        }
        return true;
    }
    
    public void addActivities(List<PmsActivityEntry> entries) {
        for(PmsActivityEntry tmp : entries) {
            entry.put(tmp.date, tmp);
        }
    }
}
