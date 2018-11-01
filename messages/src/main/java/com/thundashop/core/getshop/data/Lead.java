/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshop.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class Lead extends DataCommon {
    public static class LeadState {
        public static Integer NEW = 0;
        public static Integer HOT = 1;
        public static Integer LOST = 2;
        public static Integer WON = 3;
        public static Integer COLD = 4;
        public static Integer DELIVERED = 5;
    }
    
    public Integer leadState = 0;
    public List<LeadHistory> leadHistory = new ArrayList();
    public Integer rooms = 0;
    public Integer beds = 0;
    public String phone = "";
    public String email = "";
    public String customerName = "";
    public double offerPrice = 0.0;
    public Date followUpDate = null;
}
