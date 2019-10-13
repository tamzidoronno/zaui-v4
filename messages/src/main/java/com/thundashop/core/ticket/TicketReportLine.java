/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ticket;

import java.util.Date;

/**
 *
 * @author boggi
 */
public class TicketReportLine {

    Date startSupport;
    Date endSupport;
    String title;
    String token;
    Integer hours;
    Integer minutes;
    Integer seconds;
    boolean billable = false;
    TicketType type;
}
