/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Destination extends DataCommon {
    
    public String companyId = "";
    
    @Transient
    public Company company = new Company();
    
    public StartInfo startInfo = new StartInfo();
    
    public List<String> taskIds = new ArrayList();
    
    @Transient
    public List<Task> tasks = new ArrayList();
    
    public SkipInfo skipInfo = new SkipInfo();
    
    public String note = "";
   
    public Destination() {
        id = UUID.randomUUID().toString();
        
        company.name = "Store 1";
        company.address = new Address();
        company.address.address = "Storewille street 1";
        company.contactPerson = "Jennifer Lopez";
    }
    
    public void markHasArrived(Destination destination) {
        startInfo.lat = destination.startInfo.lat;
        startInfo.lon = destination.startInfo.lon;
        startInfo.started = destination.startInfo.started;
        startInfo.startedTimeStamp = destination.startInfo.startedTimeStamp;
    }
}