/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.support;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.getshop.data.LeadHistory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class GetShopLead extends DataCommon {

    String phone;
    String prefix;
    String email;
    String name;
    String createdByUser;
    String comment = "";

    public Integer locks = 0;
    public Integer rooms = 0;
    public Integer entrances = 0;
    public Double value = 0.0;
    public Double license = 0.0;
    public String currency = "";
    
    public LeadState state;
    
    public List<LeadHistory> leadHistory = new ArrayList();
    public Date followUpDate = null;
    public Date offerSent = null;
    
}
