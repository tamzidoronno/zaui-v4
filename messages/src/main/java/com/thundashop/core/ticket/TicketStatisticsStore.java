/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import java.util.HashMap;

/**
 *
 * @author boggi
 */
public class TicketStatisticsStore {
    public Integer count = 0;
    public String name;
    public HashMap<TicketType, Integer> counter = new HashMap();
    public Integer percentage = 0;
}
