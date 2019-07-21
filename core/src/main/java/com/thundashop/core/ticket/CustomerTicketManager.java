/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.system.SystemManager;
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
    
    @Autowired
    private SystemManager systemManager;

    @Autowired
    private MessageManager messageManager;
    
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
        ticket.urgency = ticketLight.urgency;
        
        ticket.replyToEmail = ticketLight.replyToEmail;
        ticket.replyToPrefix = ticketLight.replyToPrefix;
        ticket.replyToPhone = ticketLight.replyToPhone;
        
        ticket.userId = systemManager.getCustomerIdForStoreId(ticketLight.storeId);
        
        messageManager.sendMail("support@getshop.com", "GetShop Support", "New ticket created: " + ticket.incrementalId, "Empty", "post@getshop.com", "GetShop");
        return ticketManager.saveTicketDirect(ticket);
    }

    @Override
    public void addContent(String inStoreId, String secureTicketId, TicketContent content) {
   
        content.addedByGetShop = false;
        
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

    @Override
    public void addAttachmentToTicket(String storeId, String ticketToken, String ticketAttachmentId) {
        Ticket ticket = ticketManager.getTicketByToken(storeId, ticketToken);
        if (ticket != null) {
            ticketManager.addAttachmentToTicket(ticket.id, ticketAttachmentId);
        }
    }

}