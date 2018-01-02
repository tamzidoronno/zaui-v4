/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ITicketManager {
    public void saveTicket(Ticket ticket);
    
    @Customer
    public List<Ticket> getAllTickets(TicketFilter filter);
    
    @Editor
    public void updateEvent(String ticketId, TicketEvent event);
    
    public Ticket getTicket(String ticketId);
    
    @Administrator
    public void deleteTicket(String ticketId);
}
