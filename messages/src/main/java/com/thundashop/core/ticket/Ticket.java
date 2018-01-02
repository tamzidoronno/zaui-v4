/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Ticket extends DataCommon {
    public List<TicketEvent> events = new ArrayList();
    public String userId;
    public TicketType type;
    public TicketState currentState = TicketState.CREATED;
    public String title;
    public int incrementalId = 1;
    
    public Double timeSpent = 0D;
    public Double timeInvoice = 0D;
    
}
