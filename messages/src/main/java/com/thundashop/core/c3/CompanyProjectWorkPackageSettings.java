/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.util.HashMap;
import java.util.stream.IntStream;

/**
 *
 * @author ktonder
 */
public class CompanyProjectWorkPackageSettings {
    /**
     * Key = year
     * Value = projectcost
     */
    public HashMap<Integer, Integer> projectCost = new HashMap();

    void setProjectPrice(int year, int price) {
        projectCost.put(year, price);
    }

    int getCost(int year) {
        if (projectCost.get(year) == null)
            return 0;
        
        return projectCost.get(year);
    }
}
