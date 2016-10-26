/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class EventStatistic {
    public int year;
    public int month;
    public int count;
   
    /**
     * Key = eventId
     * Value = users
     */
    public Map<String, List<String>> users = new HashMap();
    public String locationId;
    
    public void addUserId(String eventId, String userId) {
        List<String> userList = users.get(eventId);
        if (userList == null) {
            userList = new ArrayList();
            users.put(eventId, userList);
        }
        
        userList.add(userId);
    }
}
