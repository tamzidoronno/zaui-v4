/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class TicketManager extends ManagerBase implements ITicketManager {
    public HashMap<String, Ticket> tickets = new HashMap();

    @Autowired
    public UserManager userManager;
    
    @Autowired
    public MessageManager messageManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream().forEach(inData -> {
            if (inData instanceof Ticket) {
                tickets.put(inData.id, (Ticket)inData);
            }
        });
    }
    
    @Override
    public void saveTicket(Ticket ticket) {
        checkSecurity(ticket);
        
        if (ticket.id == null || ticket.id.isEmpty()) {
            ticket.incrementalId = getNextIncrementalId();
        }
        
        saveObject(ticket);
        tickets.put(ticket.id, ticket);
    }

    private void checkSecurity(Ticket ticket) throws ErrorException {
        String userId = ticket.userId;
        
        if (getSession() == null || getSession().currentUser == null) {
            if (userId != null && !userId.isEmpty()) {
                throw new ErrorException(26);
            }
        }
        
        if (getSession() != null && getSession().currentUser != null && getSession().currentUser.type < 11 && !getSession().currentUser.id.equals(userId)) {
            throw new ErrorException(26);
        }
    }

    @Override
    public List<Ticket> getAllTickets(TicketFilter filter) {
        List<Ticket> retList = new ArrayList(tickets.values());
        
        if (filter.userId != null && !filter.userId.isEmpty()) {
            retList.removeIf(f -> f.userId == null || !f.userId.equals(filter.userId));
        }
        
        if (filter.type != null) {
            retList.removeIf(f -> f.type == null || !f.type.equals(filter.type));
        }
        
        if (filter.state != null) {
            retList.removeIf(f -> f.currentState == null || !f.currentState.equals(filter.state));
        }
        
        Collections.sort(retList, (Ticket t1, Ticket t2) -> {
            return t2.rowCreatedDate.compareTo(t1.rowCreatedDate);
        });
        
        return retList;
    }

    @Override
    public void updateEvent(String ticketId, TicketEvent event) {
        Ticket ticket = tickets.get(ticketId);
        checkSecurity(ticket);
        
        event.updatedByUserId = getSession().currentUser.id;
        
        if (event.eventType.equals(TicketEventType.OUTGOING_EMAIL)) {
            sendMail(ticket, event);
            ticket.currentState = event.state;
        }
        
        ticket.events.add(event);
        
        saveObject(ticket);
    }

    @Override
    public Ticket getTicket(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        checkSecurity(ticket);
        
        Collections.sort(ticket.events, (TicketEvent e1, TicketEvent e2) -> {
            return e2.date.compareTo(e1.date);
        });
        
        return ticket;
    }

    private void sendMail(Ticket ticket, TicketEvent event) {
        User user = userManager.getUserById(ticket.userId);
        if (user != null) {
            messageManager.sendMail(user.emailAddress, user.fullName, "Respond to ticket: " + ticket.incrementalId, event.content, "GetShop", "post@getshop.com");
        }   
    }

    private int getNextIncrementalId() {
        int i = 0;
        
        for (Ticket ticket : tickets.values()) {
            if (ticket.incrementalId > i) {
                i = ticket.incrementalId;
            }
        }
        
        i++;
        
        return i;
    }

    @Override
    public void deleteTicket(String ticketId) {
        Ticket ticket = tickets.remove(ticketId);
        if (ticket != null) {
            deleteObject(ticket);
        }
    }
    
}
