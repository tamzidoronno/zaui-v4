/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class CustomerTicketManager extends ManagerBase implements ICustomerTicketManager {
    
    @Autowired
    private TicketManager ticketManager;

    @Override
    public Ticket getTicket(String storeId, String systemGetShopComTicketId) {
        return null;
    }

    @Override
    public Ticket createTicket(TicketLight ticketLight) {
        Ticket ticket = new Ticket();
        
        ticket.createdByUserId = ticketLight.userId;
        ticket.belongsToStore = ticketLight.storeId;
        ticket.title = ticketLight.title;
        ticket.ticketToken = ticketLight.ticketToken;
        
        return ticketManager.saveTicketDirect(ticket);
    }

    @Override
    public void addContent(String inStoreId, String secureTicketId, TicketContent content) {
   
        Ticket ticket = ticketManager.getTicketByToken(inStoreId, secureTicketId);
        
        if (ticket != null) {
            ticketManager.addTicketContent(ticket.id, content);
        }
        
    }

    @Override
    public List<TicketContent> getTicketContents(String storeId, String ticketToken) {
        Ticket ticket = ticketManager.getTicketByToken(storeId, ticketToken);
        if (ticket != null) {
            return ticketManager.getTicketContents(ticket.id);
        }
        
        return new ArrayList();
    }
}