/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

/**
 *
 * @author ktonder
 */
public enum TicketEventType {
    INCOMING_EMAIL,
    INCOMING_OTHER,
    INCOMING_PHONE,
    INCOMING_SMS,
    OUTGOING_EMAIL,
    STATUS_CHANGED, 
    ASSIGNED_TO
}
