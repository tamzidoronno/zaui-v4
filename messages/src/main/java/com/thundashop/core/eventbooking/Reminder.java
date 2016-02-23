/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Reminder extends DataCommon {
    public String content = "";
    public List<String> userIds = new ArrayList();
    public String type = "";
    public String subject = "Reminder";
    public String eventId = "";
    public String sentByUserId = "";
    
    /**
     * Key = userid
     * Value = MessageID from messagemanager.
     */
    public HashMap<String, String> userIdMessageId = new HashMap();
    public HashMap<String, String> userIdInvoiceMessageId = new HashMap();
}
