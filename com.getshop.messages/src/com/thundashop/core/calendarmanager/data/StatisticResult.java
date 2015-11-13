/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.calendarmanager.data;

import com.thundashop.core.usermanager.data.Group;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class StatisticResult {
    public Group group;
    public int signedOn;
    public int waitingList;
    public String entryId;
    public String locationId; 
    
    public Date from;
    public Date to;
}
