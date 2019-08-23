/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.thundashop.core.common.DataCommon;

/**
 * This is the local ticket for the store
 * To get the full ticket use the ticketapi that is
 * connected to system.getshop.com
 * 
 * @author ktonder
 */
public class TicketLight extends DataCommon {
    public TicketState state = TicketState.CREATED;
    public String ticketToken = "";
    public String ticketId = "";
    public String title = "";
    public String userId = "";
    public String urgency = "";
    public Integer incrementalTicketId = 0;
    public String replyToEmail = "";
    public String replyToPrefix = "";
    public String replyToPhone = "";
    public TicketType type = TicketType.UNKOWN;
}