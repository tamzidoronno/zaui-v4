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
public enum TicketState {
    CREATED,
    COMPLETED,
    
    /**
     * This status is used for all tickets that 
     * we are not going to do anything more with.
     */
    CLOSED, 
    REPLIED,
    INITIAL
}
