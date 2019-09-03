/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import java.util.UUID;

/**
 *
 * @author boggi
 */
public class TicketSubTask {
    public String id = UUID.randomUUID().toString();
    public String title = "";
    public boolean completed = false;
}
