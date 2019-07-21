/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

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
}
