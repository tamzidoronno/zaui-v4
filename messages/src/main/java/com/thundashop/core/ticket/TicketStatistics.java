/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class TicketStatistics {
    public HashMap<String, TicketStatisticsStore> storeStats = new HashMap();
    public HashMap<String, Integer> ticketcounter = new HashMap();
    public int totalTicket;
    public int totalReplies = 0;
}
