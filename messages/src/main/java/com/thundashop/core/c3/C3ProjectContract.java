/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class C3ProjectContract implements Serializable {
    public int contractValue;
    public Date startDate;
    public Date endDate;
    public String id;
    
    public boolean within(Date date) { 
        if (startDate == null || endDate == null) {
            return false;
        }
        
        long StartDate1 = startDate.getTime();
        long EndDate1 = endDate.getTime(); ;
        long ldate = date.getTime();
        
        return (StartDate1 <= ldate) && (ldate <= EndDate1);
    }
}
