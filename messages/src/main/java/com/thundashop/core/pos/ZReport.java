/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.ordermanager.data.OrderTransaction;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class ZReport extends DataCommon implements Comparable<ZReport> {
    public List<String> orderIds = new ArrayList();
    public String createdByUserId = "";
    public String cashPointId = "";
    public Date start;
    public Date end;
    public double totalAmount;
    public boolean transferredToCentral = false;
    public boolean createdAfterConnectedToACentral = false;
    public List<String> invoicesWithNewPayments = new ArrayList();
    
    @Transient
    public List<PmsBookingRooms> roomsThatWillBeAutomaticallyCreatedOrdersFor;
    
    @Override
    public int compareTo(ZReport o) {
        return rowCreatedDate.compareTo(o.rowCreatedDate);
    }
}