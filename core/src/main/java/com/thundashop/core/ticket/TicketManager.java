/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.PushOver;
import com.thundashop.core.system.SystemManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public HashMap<String, TicketUserPushover> pushOverUsers = new HashMap();

    @Autowired
    public UserManager userManager;

    @Autowired
    public MessageManager messageManager;
    
    @Autowired
    public PushOver pushOver;

    @Autowired
    private SystemManager systemManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream().forEach(inData -> {
            if (inData instanceof Ticket) {
                tickets.put(inData.id, (Ticket) inData);
            }
            if (inData instanceof TicketLight) {
                lightTickets.put(inData.id, (TicketLight) inData);
            }
            if (inData instanceof TicketUserPushover) {
                pushOverUsers.put(inData.id, (TicketUserPushover) inData);
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
            retList.removeIf(f -> !f.isNotAssigned());
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
        addTicketContentInternal(ticketId, content, false);
    }
    
    private void addTicketContentInternal(String ticketId, TicketContent content, boolean silent) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            content.ticketId = ticketId;
            
            if (content.addedByGetShop) {
                content.addedByUserId = getSession().currentUser.id;
            }
            
            saveObject(content);
            
            if (silent) {
                return;
            }
            
            if (content.addedByGetShop && ticket.replyToEmail != null && !ticket.replyToEmail.isEmpty()) {
                messageManager.sendMail(ticket.replyToEmail, ticket.replyToEmail, "There has been added a new repsonse to your ticket: " + ticket.incrementalId , "Please log into your GetShop portal, go to your module and click on I Need Help, there you can see the ticketlist and find your ticket. ", "post@getshop.com", "GetShop");
            } else {
                messageManager.sendMail("support@getshop.com", "GetShop Support", "Customer has added a new content to the ticket: " + ticket.incrementalId, content.content, "post@getshop.com", "GetShop");
                String title = "New content added to ticket " + ticket.incrementalId + " ( State: " + ticket.urgency + " ) ";
                String message = content.content;
                getActivePushOverUserIds(ticketId).stream()
                        .forEach(userId -> {
                            pushOver.push(userId, title, message);
                        });
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
                .sorted((TicketContent c1, TicketContent c2) -> {
                    return c1.rowCreatedDate.compareTo(c2.rowCreatedDate);
                })
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
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            TicketState oldState = ticket.currentState;
            ticket.currentState = state;
           
            TicketEvent event = new TicketEvent();
            event.eventType = TicketEventType.STATUS_CHANGED;
            event.content = "Ticket " + ticket.incrementalId + " changed status from " + oldState.toString().toLowerCase() + " to " + ticket.currentState.toString().toLowerCase();
            ticket.events.add(event);
            saveObject(ticket);
            
            addNotificationContent(ticketId, "Ticket " + ticket.incrementalId + " changed status from " + oldState.toString().toLowerCase() +  " to " + ticket.currentState.toString().toLowerCase());
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
            
            addNotificationContent(ticketId, "Ticket has been assigned to a GetShop consultant.");
            pushOver.push(userId, "Ticket " + ticket.incrementalId + " has been assigned to your ( State: " + ticket.urgency + " )", "Ticket assigned to you...");
        }
    }   

    @Override
    public List<TicketContent> getLastTicketContent(String userId) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", "com.thundashop.core.ticket.TicketContent");
        query.append("addedByUserId", new BasicDBObject("$ne", getSession().currentUser.id));
        
        if (userId != null && !userId.isEmpty()) {
            List<String> ticketsAssignedTo = getTicketsAssignedTo(userId)
                    .stream()
                    .map(o -> o.id)
                    .collect(Collectors.toList());
            
            query.append("ticketId", new BasicDBObject("$in", ticketsAssignedTo));
        }
        
        DBCollection collection = database.getCollection("TicketManager", storeId);
        DBCursor cur = collection
                .find(query)
                .sort(new BasicDBObject("rowCreatedDate", -1))
                .limit(20);
        
        ArrayList<TicketContent> retList = new ArrayList();
        
        while (cur.hasNext()) {
            DataCommon dataCommon = database.convert(cur.next());
            retList.add((TicketContent)dataCommon);
        }

        
        retList.stream().forEach(o -> finalize(o));
        return retList;
    }

    private List<Ticket> getTicketsAssignedTo(String userId) {
        return tickets.values()
                .stream()
                .filter(o -> o.assignedToUserId != null && o.assignedToUserId.equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void markTicketAsRead(String ticketId) {
        String userId = getSession().currentUser.id;
        
        Ticket ticket = getTicket(ticketId);
        if (ticket != null) {
            List<TicketContent> contents = getTicketContents(ticketId);
            if (ticket.assignedToUserId != null && ticket.assignedToUserId.equals(userId)) {
                contents.stream().forEach(o -> {
                    o.isReadByAssignedTo = true;
                });
            }
            
            if (ticket.isNotAssigned()) {
                contents.stream().forEach(o -> {
                    o.isReadByInboxHandler = true;
                });
            }
            
            contents.stream().forEach(o -> {
                saveObject(o);
            });
        }
    }

    private void finalize(TicketContent o) {
        Ticket ticket = tickets.get(o.ticketId);
        if (ticket != null) {
            o.isAssignedToAGetShopAdmin = !ticket.isNotAssigned();
            o.ticketNumber = ticket.incrementalId;
            o.ticketTitle = ticket.title;
        }
    }

    private List<String> getActivePushOverUserIds(String ticketId) {
        boolean isWithinOfficeHours = isWithinOfficeHours();
        Ticket ticket = tickets.get(ticketId);
        
        if (ticket == null)
            return new ArrayList();
        
        boolean isUrgent = ticket.urgency != null && ticket.urgency.equals("urgent");
        
        List<String> retList = new ArrayList();
        
        for (TicketUserPushover pushOverSetting : pushOverUsers.values()) {
            // Dont notify on normal tickets outside of office hours.
            if (!isUrgent && !isWithinOfficeHours) {
                continue;
            }
            
            // Might be improved upon later, but for now all users outside of office hours receive a message if its marked as urgent.
            if (isUrgent && !isWithinOfficeHours) {
                retList.add(pushOverSetting.userId);
                continue;
            }
            
            // Receiving as its not assigned
            if (pushOverSetting.receiveMessagesForUnassignedTickets && (ticket.isNotAssigned())) {
                retList.add(pushOverSetting.userId);
                continue;
            }
            
            // Receive as its the user ticket.
            if (ticket.assignedToUserId != null && ticket.assignedToUserId.equals(pushOverSetting.userId)) {
                retList.add(pushOverSetting.userId);
            }
        }
        
        return retList;
    }

    @Override
    public void savePushOverSettings(TicketUserPushover pushOver, String pushOverToken) {
        TicketUserPushover oldObject = getPushOverSettings(pushOver.userId);
        
        userManager.addMetaData(pushOver.userId, "pushovertoken", pushOverToken);
        
        if (oldObject != null) {
            pushOver.id = oldObject.id;
        }
        
        saveObject(pushOver);
        pushOverUsers.put(pushOver.id, pushOver);
    }

    @Override
    public TicketUserPushover getPushOverSettings(String userId) {
        return pushOverUsers.values()
                .stream()
                .filter(o -> o.userId.equals(userId))
                .findAny()
                .orElse( null);
    }

    private boolean isWithinOfficeHours() {
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        
        boolean isWeekDay = dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY;
        boolean isDuringOfficeHours = hourOfDay >= 7 && hourOfDay <= 16;
        return isWeekDay && isDuringOfficeHours;
    }

    @Override
    public void reconnectTicket(String ticketId) {
        Ticket ticket = getTicket(ticketId);
        
        if (ticket != null) {
            ticket.userId = systemManager.getCustomerIdForStoreId(ticket.belongsToStore);
            saveObject(ticket);
        }
    }

    private void addNotificationContent(String ticketId, String textContent) {
        TicketContent content = new TicketContent();
        content.addedByGetShop = true;
        content.content = textContent;
        content.isReadByAssignedTo = true;
        content.isReadByInboxHandler = true;
        content.isStatusNotification = true;
        
        // This happens if the customer do modifications to nofications
        if (getSession().currentUser == null) {
            content.addedByGetShop = false;
            content.isReadByAssignedTo = false;
            content.isReadByInboxHandler = false;
        }

        addTicketContentInternal(ticketId, content, true);
    }

    public void reOpenTicket(String id) {
        Ticket ticket = tickets.get(id);
        if (ticket != null && !ticket.transferredToAccounting) {
            changeStateOfTicket(ticket.id, TicketState.CREATED);
        }
    }

    @Override
    public void changeType(String ticketId, TicketType type) {
        Ticket ticket = getTicket(ticketId);
        if (ticket != null) {
            TicketType oldType = ticket.type;
            ticket.type = type;
            saveObject(ticket);
            String oldTypeS = oldType == null ? "none" : oldType.toString().toLowerCase();
            String newTypeS = ticket.type == null ? "none" : ticket.type.toString().toLowerCase();
            addNotificationContent(ticket.id, "Ticket type was changed from " + oldTypeS + " to " + newTypeS);
        }
    }
}