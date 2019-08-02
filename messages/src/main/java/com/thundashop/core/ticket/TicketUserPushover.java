/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class TicketUserPushover extends DataCommon {
    public String userId;
    public boolean receiveMessagesForUnassignedTickets = false;
}
