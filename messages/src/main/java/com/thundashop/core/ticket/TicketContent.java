/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.thundashop.core.common.DataCommon;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class TicketContent extends DataCommon {
    public String content = "";
    public String ticketId = "";
    public boolean addedByGetShop = true;
    public String addedByUserId = "";
    public boolean isReadByInboxHandler = false;
    public boolean isReadByAssignedTo = false;
    public boolean isStatusNotification = false;
    
    @Transient
    public boolean isAssignedToAGetShopAdmin = false;
    
    @Transient
    public int ticketNumber;
    
    @Transient
    public String ticketTitle;   
}