/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public class TicketEvent {
    public TicketEventType eventType;
    public Date date = new Date();
    public String content = "";
    public TicketState state;
    public String updatedByUserId = "";
    public boolean isAnswer = false;
}
