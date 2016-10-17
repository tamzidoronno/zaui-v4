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
    
    public List<Task> tasks = new ArrayList();

    public boolean haveArrived = false;
    
    public SkipInfo skipInfo = new SkipInfo();
    
    public String note = "";
   
    @Transient
    public String destinationState = "";
    
    public Destination() {
        id = UUID.randomUUID().toString();
        
        company.name = "Store 1";
        company.address = new Address();
        company.address.address = "Storewille street 1";
        company.contactPerson = "Jennifer Lopez";
        
        for (int i = 0; i<3; i++) {
            PickupTask task = new PickupTask();
            tasks.add(task);
        }
    }

    public void calculateDestinationState() {
        if (!startInfo.started) {
            destinationState = "not_arrived";
            return;
        }
        
        if (!skipInfo.skippedReasonId.equals("")) {
            destinationState = "destination_skipped";
            return;
        }
        
        if (startInfo.completed) {
            destinationState = "completed";
            return;
        }
        
        destinationState = "normal";
    }

    public void markHasArrived(User currentUser, double lon, double lat, Date dateArrived) {
        startInfo.lat = lat;
        startInfo.lon = lon;
        startInfo.started = true;
        startInfo.startedTimeStamp = dateArrived;
    }
}