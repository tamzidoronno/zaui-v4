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
    static class LeadState {
        static Integer NEW = 0;
        static Integer HOT = 1;
        static Integer LOST = 2;
        static Integer WON = 3;
        static Integer COLD = 4;
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
