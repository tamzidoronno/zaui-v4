/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class ProMeisterScoreType {
    public List<String> categories = new ArrayList();
    public Map<String, Double> behov = new HashMap();
    public Map<String, Double> requirements = new HashMap();
    
    public ProMeisterScoreType() {
        categories.add("Bensin");
        categories.add("Diesel");
        categories.add("El/Elektronik");
        categories.add("El/Hybrid");
        categories.add("Kundbemåtande");
        
        behov.put("Bensin", 3D);
        behov.put("Diesel", 3D);
        behov.put("El/Elektronik", 3D);
        behov.put("El/Hybrid", 3D);
        behov.put("Kundbemåtande", 3D);
        
        requirements.put("Bensin", 4D);
        requirements.put("Diesel", 4D);
        requirements.put("El/Elektronik", 4D);
        requirements.put("El/Hybrid", 4D);
        requirements.put("Kundbemåtande", 4D);
    }
}
