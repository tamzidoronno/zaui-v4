/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public double sumHours;
    public String workpackages = "";
    double roundSumInKind;

    void recalcuate(double percent) {
        
        cloneIt();
        
        double newPercent = (double)percent  / 100;
        sumPost11 = (int) (sumPost11 * newPercent);
        sumHours = (int) (sumHours * newPercent);
        roundSum = (int) (roundSum * newPercent);
        roundSumInKind = (int) (roundSumInKind * newPercent);
        
        for (C3Hour hour : hours) {
            hour.cost = hour.cost * newPercent;
            hour.hours = hour.hours * newPercent;
            hour.fixedSumToUse = (int)(((double)hour.fixedSumToUse) * newPercent);
        }
        
        for (C3OtherCosts otherCost : otherCosts) {
            otherCost.cost = otherCost.cost * newPercent;
        }
    }

    private void cloneIt() {
        List<C3Hour> newHours = new ArrayList();
        for (C3Hour i : hours) {
            try {
                newHours.add((C3Hour)i.clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(C3Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        List<C3OtherCosts> newOtherCosts = new ArrayList();
        for (C3OtherCosts i : otherCosts) {
            try {
                newOtherCosts.add((C3OtherCosts)i.clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(C3Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        hours = newHours;
        otherCosts = newOtherCosts;
    }
}
