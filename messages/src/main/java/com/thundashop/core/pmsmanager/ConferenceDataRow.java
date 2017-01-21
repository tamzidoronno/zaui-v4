/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class ConferenceDataRow implements Serializable {
    public String rowId = UUID.randomUUID().toString();
    public String from;
    public String to;
    public String place;
    public String actionName;
    public String attendeesCount;
}
