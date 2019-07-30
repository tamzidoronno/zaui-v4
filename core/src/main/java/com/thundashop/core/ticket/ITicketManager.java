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
    @Administrator
    public void saveTicket(Ticket ticket);
    
    @Customer
    public List<Ticket> getAllTickets(TicketFilter filter);
    
    @Editor
    public void updateEvent(String ticketId, TicketEvent event);
    
    @Administrator
    public Ticket getTicket(String ticketId);
    
    @Administrator
    public void deleteTicket(String ticketId);
    
    @Administrator
    public TicketLight createLightTicket(String title);
    
    @Administrator
    public List<TicketLight> getTicketLights();
    
    public Ticket getTicketByToken(String storeId, String ticketToken);
    
    @Administrator
    public List<TicketContent> getTicketContents(String ticketId);
    
    public void updateTicket(String ticketToken, TicketLight light);

    @Administrator
    public void addAttachmentToTicket(String ticketId, String ticketAttachmentId);
    
    public void uploadAttachment(TicketAttachment attachment);
    
    public TicketAttachment getAttachment(String attachmentId);
    
    @Administrator
    public String getTicketIdByToken(String ticketToken);
    
    @Administrator
    public void addTicketContent(String ticketId, TicketContent content);
    
    @Administrator
    public void changeStateOfTicket(String ticketId, TicketState state);
    
    @Administrator
    public void assignTicketToUser(String ticketId, String userId);
    
    @Administrator
    public List<TicketContent> getLastTicketContent(String userId);
    
    @Administrator
    public void markTicketAsRead(String ticketId);
    
    @Administrator
    public void savePushOverSettings(TicketUserPushover pushOver, String pushOverToken);
    
    @Administrator
    public TicketUserPushover getPushOverSettings(String userId);
    
    @Administrator
    public void reconnectTicket(String ticketId);
    
    @Administrator
    public void changeType(String ticketId, TicketType type);
}
