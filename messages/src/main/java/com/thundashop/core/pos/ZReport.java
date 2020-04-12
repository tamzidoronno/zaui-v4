/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ZReport extends DataCommon implements Comparable<ZReport> {
    public List<String> orderIds = new ArrayList();
    public List<String> retransmittedOrderIds = new ArrayList();
    public String createdByUserId = "";
    public String cashPointId = "";
    public Date start;
    public Date end;
    public double totalAmount;
    public boolean transferredToCentral = false;
    public boolean createdWhileConnectedToCentral = false;
    
    @Override
    public int compareTo(ZReport o) {
        return rowCreatedDate.compareTo(o.rowCreatedDate);
    }
}