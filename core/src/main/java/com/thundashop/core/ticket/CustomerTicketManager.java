/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.system.SystemManager;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

        return ticketManager.saveTicketDirect(ticket);
    }

    @Override
    public void addContent(String inStoreId, String secureTicketId, TicketContent content) {
   
        content.addedByGetShop = false;
        
        Ticket ticket = ticketManager.getTicketByToken(inStoreId, secureTicketId);
        
        if (ticket != null) {
            ticketManager.resetTicket(ticket.id);
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

    @Override
    public void reOpenTicket(String storeId, String ticketToken) {
        Ticket ticket = ticketManager.getTicketByToken(storeId, ticketToken);
        if (ticket != null) {
            ticketManager.reOpenTicket(ticket.id);
        }
    }

    @Override
    public List<Ticket> getPredefinedTickets() {
        TicketFilter filter = new TicketFilter();
        filter.state = TicketState.INITIAL;
        filter.type = TicketType.SETUP;
        
        return ticketManager.getAllTickets(filter);
    }
    
    @Override
    public Ticket cloneSetupTicket(String ticketId, String storeId) {
        SecureRandom random = new SecureRandom();
        Ticket originalTicket = ticketManager.getTicket(ticketId);
        Gson gson = new Gson();
        String clonedtext = gson.toJson(originalTicket);
        Ticket copy = gson.fromJson(clonedtext, Ticket.class);
        copy.type = TicketType.SETUP;
        copy.currentState = TicketState.CREATED;
        copy.id = null;
        copy.ticketToken = new BigInteger(130, random).toString(32);
        copy.belongsToStore = storeId;
        copy.userId = systemManager.getCustomerIdForStoreId(storeId);
        ticketManager.saveTicketDirect(copy);
        
        List<TicketContent> contents = ticketManager.getTicketContents(ticketId);
        for(TicketContent con : contents) {
            clonedtext = gson.toJson(con);
            TicketContent contentcopy = gson.fromJson(clonedtext, TicketContent.class);
            contentcopy.ticketId = copy.id;
            contentcopy.id = null;
            ticketManager.saveContent(contentcopy);
        }
        
        return copy;
    }

    @Override
    public TicketStatisticsStore getStoreStatistics(TicketStatsFilter filter) {
        filter.userId = systemManager.getCustomerIdForStoreId(filter.storeId);
        return ticketManager.getStoreStatistics(filter);
    }

    @Override
    public TicketNotifications getNiggerFriendlyTicketNotifications(String storeId) {
        List<UnreadTickets> unreadTickets = ticketManager.getUnreadTickets(storeId);
        TicketNotifications result = new TicketNotifications();
        result.numberOfUnReadTickets = unreadTickets.size();
        for(UnreadTickets t : unreadTickets) {
            result.unreadTickets.put(t.tokenId, t.title);
        }
        return result;
    }

    @Override
    public void markTicketAsRead(String tokenId) {
        ticketManager.markAsRead(tokenId);
    }

    @Override
    public TicketReport getTicketReportForCustomer(Date start, Date end, String storeId) {
        TicketReport report = new TicketReport();
        report.start = start;
        report.end = end;
        
        List<TicketReportLine> tickets = ticketManager.getAllTicketsRepliedToBetween(start, end, storeId);
        report.lines = tickets;
        
        report.summarizeSupport();
        
        
        String userId = systemManager.getCustomerIdForStoreId(storeId);
        report.hoursIncluded = ticketManager.getHoursIncluded(userId);

        return report;
    }
    

}