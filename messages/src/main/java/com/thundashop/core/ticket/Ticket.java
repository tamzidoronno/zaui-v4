/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Ticket extends DataCommon {
    public List<TicketEvent> events = new ArrayList();
    
    /**
     * The GetShop Customer ID
     */
    public String userId;
    public TicketType type;
    public TicketState currentState = TicketState.CREATED;
    public String title;
    public int incrementalId = 1;
    public String externalId = "";
    
    public Date dateCompleted = null;
   
    public String assignedToUserId = "";
    
    /**
     * Created by store and userid from the store.
     */
    public String belongsToStore = "";
    public String createdByUserId = "";
    
    /** 
     * This ID correspond to the TicketLight.ticketToken
     */
    public String ticketToken = "";
    
    /**
     * in minutes
     */
    public Double timeSpent = 0D;
    public Double timeInvoice = 0D;
    public boolean hasBeenValidedForTimeUsage = false;
            
    public boolean transferredToAccounting = false;
    public String productId = "";

    public List<String> attachmentIds = new ArrayList();
    public String urgency = "";
    public String replyToPhone;
    public String replyToPrefix;
    public String replyToEmail;
    
    public Date getAddonInvoiceDate() {
        if (!currentState.equals(TicketState.COMPLETED)) {
            return null;
        }
        
        if (dateCompleted == null) {
            return null;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateCompleted);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, 1);
        
        
        Date date = cal.getTime();
        
        if (isDateInPast(cal.getTime())) {
            return getFirstDateInNextMonth();
        }
        
        return cal.getTime();
    }

    private static boolean isDateInPast(Date cal) {
        Date now = new Date();
        return cal.before(now);
    }

    private Date getFirstDateInNextMonth() {
        Date date = new Date();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, 1);
        
        return cal.getTime();
    }

    void setCompletedDate() {
        if (dateCompleted == null && currentState.equals(TicketState.COMPLETED)) {
            dateCompleted = new Date();
        }
    }
}
