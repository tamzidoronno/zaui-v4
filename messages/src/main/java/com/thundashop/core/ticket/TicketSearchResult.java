/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */
public class TicketSearchResult {
    public Integer incId;
    public String ticketId;
    public String title;
    public Integer replies;
    public Date createdDate;
    public Date lastRepliedDate;
    public String byWho = "";
    public List<String> resultStrings = new ArrayList();
}
