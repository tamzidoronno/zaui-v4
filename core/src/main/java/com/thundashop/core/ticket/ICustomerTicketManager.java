/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.List;
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ICustomerTicketManager {
    public Ticket getTicket(String storeId, String secureTicketId);
    public Ticket createTicket(TicketLight ticketLight);
    
    public List<TicketContent> getTicketContents(String storeId, String ticketToken);
    public void addContent(String storeId, String secureTicketId, TicketContent content);
    public void addAttachmentToTicket(String storeId, String ticketToken, String ticketAttachmentId);
    
    public void reOpenTicket(String storeId, String ticketToken);
    public List<Ticket> getPredefinedTickets();
    public Ticket cloneSetupTicket(String ticketId, String storeId);
    public TicketStatisticsStore getStoreStatistics(TicketStatsFilter filter);

    /**
     * A customer (black man) had a problem where he was not able to find the tickets replied to, 
     * so he just complained about not getting any answers even if we had answered all his question.
     * Something had to be done.
     * @return 
     */
    @ForceAsync
    public TicketNotifications getNiggerFriendlyTicketNotifications(String storeId);
    @ForceAsync
    public void markTicketAsRead(String tokenId);
    
    @ForceAsync
    public TicketReport getTicketReportForCustomer(Date start, Date end, String storeId, TicketType type);
}
