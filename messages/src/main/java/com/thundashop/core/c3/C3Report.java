/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class C3Report {
    public String userId;
    public String projectId;
    
    public Date startDate;
    public Date endDate;
    
    public double roundSum;
    public List<C3Hour> hours = new ArrayList();
    public List<C3OtherCosts> otherCosts = new ArrayList();
    
    public int sumPost11;
    public int sumHours;
}
