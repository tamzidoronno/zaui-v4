/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class TicketManager extends ManagerBase implements ITicketManager {

    public HashMap<String, Ticket> tickets = new HashMap();
    public HashMap<String, TicketLight> lightTickets = new HashMap();

    @Autowired
    public UserManager userManager;

    @Autowired
    public MessageManager messageManager;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream().forEach(inData -> {
            if (inData instanceof Ticket) {
                tickets.put(inData.id, (Ticket) inData);
            }
            if (inData instanceof TicketLight) {
                lightTickets.put(inData.id, (TicketLight) inData);
            }
        });
    }

    @Override
    public void saveTicket(Ticket ticket) {
        checkSecurity(ticket);

        saveTicketDirect(ticket);
        
    }

    public Ticket saveTicketDirect(Ticket ticket) throws ErrorException {
        if (ticket.id == null || ticket.id.isEmpty()) {
            ticket.incrementalId = getNextIncrementalId();
        }

        ticket.setCompletedDate();
        saveObject(ticket);
        tickets.put(ticket.id, ticket);
        
        return ticket;
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
            if (filter.type.equals(TicketType.UNKOWN)) {
                retList.removeIf(f -> f.type != null && !f.type.equals(filter.type));
            } else {
                retList.removeIf(f -> f.type == null || !f.type.equals(filter.type));
            }
        }

        if (filter.state != null) {
            retList.removeIf(f -> f.currentState == null || !f.currentState.equals(filter.state));
        }
        
        if (filter.uassigned) {
            retList.removeIf(f -> f.assignedToUserId != null && !f.assignedToUserId.isEmpty());
        }
        
        if (filter.assignedTo != null && !filter.assignedTo.isEmpty()) {
            retList.removeIf(f -> f.assignedToUserId == null || !f.assignedToUserId.equals(filter.assignedTo));
        }
        
        if (filter.checkForBilling) {
            retList.removeIf(f -> f.hasBeenValidedForTimeUsage || f.incrementalId <= 359);
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
        }

        ticket.currentState = event.state;

        ticket.setCompletedDate();
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
//        We wait with this until we are its more finished.
//        User user = userManager.getUserById(ticket.userId);
//        if (user != null) {
//            messageManager.sendMail(user.emailAddress, user.fullName, "Respond to ticket: " + ticket.incrementalId, event.content, "GetShop", "post@getshop.com");
//        }   
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

    public List<Ticket> getTicketsToTransferToAccounting() {
        Date lastDayInPrevMonth = getLastDateInPrevMonth();

        List<Ticket> ticketsToTransfer = tickets.values().stream()
                .filter(ticket -> !ticket.transferredToAccounting)
                .filter(ticket -> ticket.currentState.equals(TicketState.COMPLETED))
                .filter(ticket -> ticket.dateCompleted.before(lastDayInPrevMonth) || ticket.dateCompleted.equals(lastDayInPrevMonth) )
                .filter(ticket -> ticket.timeInvoice > 0)
                .collect(Collectors.toList());

        return ticketsToTransfer;
    }

    public void markTicketAsTransferredToAccounting(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            ticket.transferredToAccounting = true;
            saveObject(ticket);
        }
    }

    private Date getLastDateInPrevMonth() {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.add(Calendar.MONTH, -1);
        aCalendar.set(Calendar.DATE, 1);

        aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        
        aCalendar.set(Calendar.MINUTE, 59);
        aCalendar.set(Calendar.SECOND, 59);
        aCalendar.set(Calendar.MILLISECOND, 999);
    
        Date lastDateOfPreviousMonth = aCalendar.getTime();
        
        logPrint(lastDateOfPreviousMonth);
    
        
        return lastDateOfPreviousMonth;
    }

    @Override
    public TicketLight createLightTicket(String title) {
        SecureRandom random = new SecureRandom();
        
        TicketLight lightTicket = new TicketLight();
        lightTicket.title = title;
        lightTicket.userId = getSession().currentUser.id;
        lightTicket.ticketToken = new BigInteger(130, random).toString(32);
        
        saveObject(lightTicket);
        lightTickets.put(lightTicket.id, lightTicket);
        return lightTicket;
    }

    public List<TicketLight> getTicketLights() {
        ArrayList<TicketLight> retList = new ArrayList(lightTickets.values());
        retList.sort((TicketLight a, TicketLight b) -> {
            return b.rowCreatedDate.compareTo(a.rowCreatedDate);
        });
        return retList;
    }

    @Override
    public void updateTicket(String ticketToken, TicketLight light) {
        TicketLight existingLightToken = lightTickets.values()
                .stream()
                .filter(o -> o.ticketToken != null && !o.ticketToken.isEmpty() && o.ticketToken.equals(ticketToken))
                .findFirst()
                .orElse(null);
                
        
        if (existingLightToken == null) {
            return;
        }
        
        light.id = existingLightToken.id;
        saveObject(light);
        lightTickets.put(light.id, light);
    }

    @Override
    public String getTicketIdByToken(String ticketToken) {
        return tickets.values()
                .stream()
                .filter(o -> o.ticketToken != null && !o.ticketToken.isEmpty() && o.ticketToken.equals(ticketToken))
                .findAny()
                .map(o -> o.id)
                .orElse(null);
    }
    
    @Override
    public Ticket getTicketByToken(String storeId, String ticketToken) {
        return tickets.values()
                .stream()
                .filter(o -> o.belongsToStore != null && !o.belongsToStore.isEmpty() && o.belongsToStore.equals(storeId))
                .filter(o -> o.ticketToken != null && !o.ticketToken.isEmpty() && o.ticketToken.equals(ticketToken))
                .findAny()
                .orElse(null);
    }

    @Override
    public void addTicketContent(String ticketId, TicketContent content) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            content.ticketId = ticketId;
            
            if (content.addedByGetShop) {
                content.addedByUserId = getSession().currentUser.id;
            }
            
            saveObject(content);
            
            if (content.addedByGetShop && ticket.replyToEmail != null && !ticket.replyToEmail.isEmpty()) {
                messageManager.sendMail(ticket.replyToEmail, ticket.replyToEmail, "There has been added a new repsonse to your ticket: " + ticket.incrementalId , "Please log into your GetShop portal, go to your module and click on I Need Help, there you can see the ticketlist and find your ticket. ", "post@getshop.com", "GetShop");
            } else {
                messageManager.sendMail("support@getshop.com", "GetShop Support", "Customer has added a new content to the ticket: " + ticket.incrementalId, content.content, "post@getshop.com", "GetShop");
            }
        }
    }

    @Override
    public List<TicketContent> getTicketContents(String ticketId) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", "com.thundashop.core.ticket.TicketContent");
        query.put("ticketId", ticketId);
        return database.query("TicketManager", storeId, query)
                .stream()
                .map( o -> (TicketContent)o)
                .collect(Collectors.toList());
    }

    @Override
    public void uploadAttachment(TicketAttachment attachment) {
        if (attachment.id == null || attachment.id.isEmpty()) {
            throw new ErrorException(26);
        }
        
        saveObject(attachment);
    }

    @Override
    public void addAttachmentToTicket(String ticketId, String ticketAttachmentId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            ticket.attachmentIds.add(ticketAttachmentId);
            saveObject(ticket);
        }
    }

    @Override
    public TicketAttachment getAttachment(String attachmentId) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", "com.thundashop.core.ticket.TicketAttachment");
        query.put("_id", attachmentId);
       
        return database.query("TicketManager", storeId, query)
                .stream()
                .map( o -> (TicketAttachment)o)
                .findAny()
                .orElse(null);
    }

    @Override
    public void changeStateOfTicket(String ticketId, TicketState state) {
        Ticket ticket = getTicket(ticketId);
        if (ticket != null) {
            TicketState oldState = ticket.currentState;
            ticket.currentState = state;
           
            TicketEvent event = new TicketEvent();
            event.eventType = TicketEventType.STATUS_CHANGED;
            event.content = "Ticket changed status from " + oldState + " to " + ticket.currentState;
            ticket.events.add(event);
            saveObject(ticket);
            
            notifyEventChanged(ticket, event);
        }
    }

    private void notifyEventChanged(Ticket ticket, TicketEvent event) {
        // TODO..
    }

    @Override
    public void assignTicketToUser(String ticketId, String userId) {
        Ticket ticket = getTicket(ticketId);
        if (ticket != null) {
            ticket.assignedToUserId = userId;
           
            TicketEvent event = new TicketEvent();
            event.eventType = TicketEventType.ASSIGNED_TO;
            event.content = "Ticket has been assigned to a new GetShop Consultant";
            ticket.events.add(event);
            saveObject(ticket);
            
            notifyEventChanged(ticket, event);
        }
    }   
}