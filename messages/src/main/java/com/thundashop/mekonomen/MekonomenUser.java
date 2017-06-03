/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.mekonomen;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class MekonomenUser {
    public String firstName;
    public String sureName;	
    public String groupName;
    public String befattning;
    public String managerLogin;
    public String username;
    public String email;

    public List<MekonomenEventParticipant> events = new ArrayList();
    
    @Override
    public String toString() {
        return firstName+","+sureName+","+groupName+","+befattning+","+managerLogin+","+username+","+email;
    }
    
    public String getFullName() {
        if (firstName == null)
            firstName = "";
        
        if (sureName == null)
            sureName = "";
        
        return firstName + " " + sureName;
    }
    
    public void add(MekonomenEvent event, MekonomenEventParticipant participant) {
        participant.event = event;
        events.add(participant);
    }
}
