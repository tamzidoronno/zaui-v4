/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.Company;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    /**
     * Signature is saved as PNG base 64 encoded images.
     */
    public String signatureImage = "";
    
    public SkipInfo skipInfo = new SkipInfo();
    
    public String note = "";
    public Integer seq;
    public String podBarcode = "";
   
    public Destination() {
    }
    
    public void markHasArrived(Destination destination) {
        startInfo.lat = destination.startInfo.lat;
        startInfo.lon = destination.startInfo.lon;
        startInfo.started = destination.startInfo.started;
        startInfo.startedTimeStamp = destination.startInfo.startedTimeStamp;
    }

    void ensureUniqueTaskIds() {
        Set<String> hs = new HashSet<>();
        hs.addAll(taskIds);
        taskIds.clear();
        taskIds.addAll(hs);
    }
}