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
import com.thundashop.core.common.PermenantlyDeleteData;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.PushOver;
import com.thundashop.core.system.SystemManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
    public HashMap<String, UnreadTickets> unreadTickets = new HashMap();

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
            if (inData instanceof UnreadTickets) {
                unreadTickets.put(((UnreadTickets) inData).ticketId, (UnreadTickets) inData);
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
        
        if(filter.start != null) {
            retList.removeIf(f -> f.rowCreatedDate.before(filter.start));
        }
        if(filter.end != null) {
            retList.removeIf(f -> f.rowCreatedDate.after(filter.end));
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

    @Override
    public List<TicketLight> getTicketLights() {
        ArrayList<TicketLight> retList = new ArrayList(lightTickets.values());
        retList.sort((TicketLight a, TicketLight b) -> {
            return b.rowCreatedDate.compareTo(a.rowCreatedDate);
        });
        
        List<TicketLight> remove = new ArrayList();
        for(TicketLight l : retList) {
            if(l.type == TicketType.SETUP) {
                remove.add(l);
            }
        }
        retList.removeAll(remove);
        
        
        return retList;
    }

    @Override
    public List<TicketLight> getSetupTicketsLights() {
        ArrayList<TicketLight> retList = new ArrayList(lightTickets.values());
        retList.sort((TicketLight a, TicketLight b) -> {
            return b.rowCreatedDate.compareTo(a.rowCreatedDate);
        });
        
        List<TicketLight> remove = new ArrayList();
        for(TicketLight l : retList) {
            if(l.type != TicketType.SETUP) {
                remove.add(l);
            }
        }
        retList.removeAll(remove);
        
        
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
        Ticket ticket = tickets.values()
                .stream()
                .filter(o -> o.belongsToStore != null && !o.belongsToStore.isEmpty() && o.belongsToStore.equals(storeId))
                .filter(o -> o.ticketToken != null && !o.ticketToken.isEmpty() && o.ticketToken.equals(ticketToken))
                .findAny()
                .orElse(null);
        
        TicketLight lightTicket = getLightTicketByToken(ticketToken);
        if(lightTicket != null) {
            lightTicket.state = ticket.currentState;
            saveObject(lightTicket);
        }
        
        return ticket;
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
    
    public void resetTicket(String id) {
        Ticket ticket = tickets.get(id);
        if (ticket != null) {
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

    @Override
    public void markAsRepied(String ticketId) {
        Ticket ticket = getTicket(ticketId);
        if (ticket != null) {
            ticket.currentState = TicketState.REPLIED;
            saveObject(ticket);
        }
    }

    @Override
    public String createSetupTicket(String title) {
        Ticket ticket = new Ticket();
        ticket.title = title;
        ticket.type = TicketType.SETUP;
        ticket.currentState = TicketState.INITIAL;
        saveTicketDirect(ticket);
        
        TicketContent content = new TicketContent();
        content.addedByGetShop = true;
        content.content = "Content goes here";
        content.isReadByAssignedTo = true;
        content.isReadByInboxHandler = true;
        content.isStatusNotification = true;
        
        addTicketContentInternal(ticket.id, content, true);
        
        return ticket.id;
    }

    @Override
    public void updateContent(String ticketId, String contentId, String content) {
        for(TicketContent con : getTicketContents(ticketId)) {
            if(con.id.equals(contentId)) {
                con.content = content;
                saveObject(con);
            }
        }
    }

    @Override
    public void addSubTask(String ticketId, String title) {
        TicketSubTask subtask = new TicketSubTask();
        subtask.title = title;
        
        Ticket ticket = getTicket(ticketId);
        ticket.subtasks.put(subtask.id, subtask);
        saveTicket(ticket);
    }

    @Override
    public void deleteSubTask(String ticketId, String subTaskId) {
        Ticket ticket = getTicket(ticketId);
        ticket.subtasks.remove(subTaskId);
        saveTicket(ticket);
    }

    @Override
    public void toggleSubTask(String ticketId, String subTaskId) {
        Ticket ticket = getTicket(ticketId);
        ticket.subtasks.get(subTaskId).completed = !ticket.subtasks.get(subTaskId).completed;
        saveTicket(ticket);
    }

    void updateLightTicket(TicketLight light) {
        saveObject(light);
        lightTickets.put(light.id, light);
    }
    
    @Override
    public TicketLight createLightTicketOfClonedSetupTicket(Ticket ticket) {
        TicketLight light = createLightTicket(ticket.title);
        light.state = TicketState.CREATED;
        light.type = TicketType.SETUP;
        light.ticketId = ticket.id;
        light.ticketToken = ticket.ticketToken;
        light.ticketId = ticket.id;
        light.incrementalTicketId = ticket.incrementalId;
        updateLightTicket(light);
        return light;
    }

    void saveContent(TicketContent contentcopy) {
        saveObject(contentcopy);
    }

    @Override
    public TicketStatistics getStatistics(TicketStatsFilter filter) {
        TicketStatistics stats = new TicketStatistics();
        stats.totalTicket = 0;
        TicketFilter filtertofind = new TicketFilter();
        filtertofind.userId = filter.userId;
        
        List<Ticket> allTickets = getAllTickets(filtertofind);
        for(Ticket ticket : allTickets) {
            String toStore = ticket.belongsToStore;
            if(toStore == null || toStore.isEmpty()) {
                continue;
            }
            String customerId = systemManager.getCustomerIdForStoreId(toStore);
            User usr = userManager.getUserById(customerId);
            if(usr == null ) {
                continue;
            }
            
            Double timeSpentInPeriode = 0.0;
            Double timeInvoicedInPeriode = 0.0;
            if(filter.start != null) {
                timeSpentInPeriode = ticket.getTimeSpentInPeriode(filter.start, filter.end);
            }
            if(filter.start != null) {
                timeInvoicedInPeriode = ticket.getTimeInvoicedInPeriode(filter.start, filter.end);
            }
            
            if(timeInvoicedInPeriode == 0.0 && timeSpentInPeriode == 0.0) {
                continue;
            }
            
            Company comp = userManager.getCompany(usr.mainCompanyId);
            
            TicketStatisticsStore storeStats = new TicketStatisticsStore();
            if(stats.storeStats.containsKey(customerId)) {
                storeStats = stats.storeStats.get(customerId);
            }
            storeStats.count++;
            stats.storeStats.put(customerId, storeStats);
            storeStats.name = usr.fullName; 
            storeStats.hoursSpent += timeSpentInPeriode;
            storeStats.hoursInvoiced += timeInvoicedInPeriode;
            if(comp != null) {
                storeStats.hoursIncluded = comp.monthlyHoursIncluded;
                storeStats.additonalHours = comp.additionalHours;
            }
            stats.totalTicket++;
        }
        
        for(TicketStatisticsStore stat : stats.storeStats.values()) {
            stat.percentage = (int)(((double)stat.count / (double)stats.totalTicket) * 100);
        }
        
        if(filter.userId != null && !filter.userId.isEmpty() && !stats.storeStats.containsKey(filter.userId)) {
            User usr = userManager.getUserById(filter.userId);
            Company comp = userManager.getCompany(usr.mainCompanyId);
            if(comp != null) {
                TicketStatisticsStore stats11 = new TicketStatisticsStore();
                stats11.name = usr.fullName;
                stats11.hoursIncluded = comp.monthlyHoursIncluded;
                stats11.additonalHours = comp.additionalHours;
                stats.storeStats.put(filter.userId, stats11);
            }
        }
        
        return stats;
    }


    Integer getHoursIncluded(String userId) {
        User usr = userManager.getUserById(userId);
        Company comp = userManager.getCompany(usr.mainCompanyId);
        if(comp != null) {
            return comp.monthlyHoursIncluded + comp.additionalHours;
        }
        return 0;
    }
        
    
    @Override
    public TicketStatisticsStore getStoreStatistics(TicketStatsFilter filter) {
        TicketStatistics statistics = getStatistics(filter);
        return statistics.storeStats.get(filter.userId);
    }

    @Override
    public void markTicketAsUnread(String ticketId) {
        if(unreadTickets.containsKey(ticketId)) {
            return;
        }
        
        Ticket ticket = getTicket(ticketId);
        UnreadTickets unread = new UnreadTickets();
        unread.ticketId = ticket.id;
        unread.tokenId = ticket.ticketToken;
        unread.title = ticket.title;
        unread.belongsToStore = ticket.belongsToStore;
        unreadTickets.put(ticket.id, unread);
        saveObject(unread);
    }

    void markAsRead(String tokenId) {
        UnreadTickets toremove = null;
        for(UnreadTickets t : unreadTickets.values()) {
            if(t.tokenId.equals(tokenId)) {
                toremove = t;
            }
        }
        if(toremove != null) {
            unreadTickets.remove(toremove.ticketId);
            deleteObject(toremove);
        }
    }

    public List<UnreadTickets> getUnreadTickets(String storeId) {
        List<UnreadTickets>  result = new ArrayList();
        for(UnreadTickets t : unreadTickets.values()) {
            if(t.belongsToStore.equals(storeId)) {
                result.add(t);
            }
        }
        return result;
    }

    private TicketLight getLightTicketByToken(String ticketToken) {
        for(TicketLight l : lightTickets.values()) {
            if(l.ticketToken.equals(ticketToken)) {
                return l;
            }
        }
        return null;
    }

    @Override
    public void updateLightTicketState(String ticketToken, TicketState state) {
        TicketLight lightTicket = getLightTicketByToken(ticketToken);
        lightTicket.state = state;
        saveObject(lightTicket);
    }

    List<TicketReportLine> getAllTicketsRepliedToBetween(Date start, Date end, String storeId) {
        List<TicketReportLine> lines = new ArrayList();
        String usrId = systemManager.getCustomerIdForStoreId(storeId);
        
        for(Ticket ticket : tickets.values()) {
            String userId = systemManager.getCustomerIdForStoreId(ticket.belongsToStore);
            
            if(usrId == null || userId == null) {
                continue;
            }
            if(!usrId.equals(userId)) {
                continue;
            }
            
            
            for(Long timeEnd : ticket.timeSpentAtDate.keySet()) {
                if(timeEnd > start.getTime() && timeEnd < end.getTime()) {
                    long millisecondsspent = (long)(ticket.timeSpentAtDate.get(timeEnd) * 3600000);
                    long timeStart = timeEnd - millisecondsspent;
                    Date timeStartDate = new Date();
                    timeStartDate.setTime(timeStart);
                    Date timeEndDate = new Date();
                    timeEndDate.setTime(timeEnd);
                    
                    TicketReportLine reportLine = new TicketReportLine();
                    reportLine.startSupport = timeStartDate;
                    reportLine.endSupport = timeEndDate;
                    reportLine.title = ticket.title;
                    if(ticket.timeInvoiceAtDate.get(timeEnd) != null && ticket.timeInvoiceAtDate.get(timeEnd) > 0) {
                        reportLine.billable = true;
                    }
                    reportLine.token = ticket.ticketToken;
                    
                    setDifferenceOnLine(timeStartDate, timeEndDate, reportLine);
                    lines.add(reportLine);
                }
            }
        }
        
        lines.sort(new Comparator<TicketReportLine>() {
            @Override
            public int compare(TicketReportLine m1, TicketReportLine m2) {
                return m2.startSupport.compareTo(m1.startSupport);
             }
        });

        
        return lines;
    }
    
    private void setDifferenceOnLine(Date startDate, Date endDate, TicketReportLine line){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        line.hours = (int)elapsedHours;
        line.minutes = (int)elapsedMinutes;
        line.seconds = (int)elapsedSeconds;

    }

}