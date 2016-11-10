/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class Route extends DataCommon {
    public String name = "Routename";
    public int seq = 0;
    
    public String shortDescription = "";

    public StartInfo startInfo = new StartInfo();
    
    public List<String> destinationIds = new ArrayList();
    
    public List<String> userIds = new ArrayList();
    
    @Transient
    private List<Destination> destinations = new ArrayList();
    

    public Route() {
        shortDescription = "A route description";
    }

    public boolean hasDestinationId(String destinationId) {
        return destinationIds.contains(destinationId);
    }
    
    public void addDestination(Destination dest) {
        if (!destinationIds.contains(dest.id)) {
            throw new ErrorException(1000016);
        }
        
        destinations.removeIf(o -> o.id.equals(dest.id));
        destinations.add(dest);
    }


    public List<Destination> getDestinations() {
        return destinations;
    }

    void makeSureUserIdsNotDuplicated() {
        
        // add elements to al, including duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(userIds);
        userIds.clear();
        userIds.addAll(hs);
    }

}
