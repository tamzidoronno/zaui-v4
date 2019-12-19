/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 *
 * @author boggi
 */
public class PmsCustomerReportEntry {
    public String userId = "";
    public Integer numberOfNights = 0;
    public Double totalSlept = 0.0;
    public Double total = 0.0;
    HashMap<String, BigDecimal> productValues;
    HashMap<String, Integer> productCount;
    String fullName;
    
    
    public Integer getNumberSlept() {
        return numberOfNights;
    }
}
