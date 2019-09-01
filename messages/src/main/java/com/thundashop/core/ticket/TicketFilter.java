/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public class TicketFilter {
    public String userId = "";
    public TicketState state;
    public TicketType type;
    public String assignedTo = "";
    public boolean checkForBilling = false;
    public boolean uassigned = false;
    public Date end;
    public Date start;
}
