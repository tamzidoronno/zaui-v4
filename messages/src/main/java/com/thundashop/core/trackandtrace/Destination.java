/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.PermenantlyDeleteData;
import com.thundashop.core.usermanager.data.Company;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
@PermenantlyDeleteData
public class Destination extends DataCommon {
    
    public List<String> companyIds = new ArrayList();
    
    @Transient
    public Company company = new Company();
    
    public StartInfo startInfo = new StartInfo();
    
    public List<String> taskIds = new ArrayList();
    
    @Transient
    public List<Task> tasks = new ArrayList();

    @Transient
    /**
     * Signature is saved as PNG base 64 encoded images.
     */
    public String signatureImage = "";
    
    public List<TrackAndTraceSignature> signatures = new ArrayList();
    
    public String typedNameForSignature = "";
    
    public SkipInfo skipInfo = new SkipInfo();
    
    public String exceptionId = "";
    
    public Integer seq;
    public String podBarcode = "";
    
    public String deliveryInstruction = "";
    public String pickupInstruction = "";
    public String onDemandInstructions = "" ;
    public String extraInstructions = "" ;
    
    public String stopWindow;
    
    public boolean dirty = false;
    public Date movedFromPool;
    public boolean signatureRequired = true;
    public boolean extraInstractionsRead = false;
    public Date extraInstractionsReadDate = null;
    
    public String collectionPayType = "";
    
    public String customerNumber = "";
    
    public List<CollectionTasks> collectionTasks = new ArrayList();
   
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
    
    void setPodBarcodeStringToTasks() {
        tasks.stream().forEach(task -> {
            if (task != null)
                task.setPodBarcodeStringToTasks();
        });
    }

    public TrackAndTraceSignature getLatestSignatureImage() {
        TrackAndTraceSignature signature = null;
        
        for (TrackAndTraceSignature sing : signatures) {
            if (sing.imageId != null && sing.imageId.contains("Found")) {
                continue;
            }
            if (signature == null) {
                signature = sing;
            } else {
                if (sing.sigutureAddedDate.after(signature.sigutureAddedDate)) {
                    signature = sing;
                }
            }
        }
        
        if (signature == null)
            return null;
        
        return signature;
    }

    DeliveryTask getDeliveryTask() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public PickupTask getPickupTask() {
        for (Task task : tasks) {
            if (task instanceof PickupTask) {
                return (PickupTask)task;
            }
        }
        
        return null;
    }

    public void unStart() {
        signatures.clear();
    }
    
    public List<String> getPodBarcodes() {
        Set<String> podBarcodes = new TreeSet();
        tasks.stream().forEach(t -> {
            podBarcodes.add(t.podBarcode);
        });
        
        return new ArrayList(podBarcodes);
    }

    public CollectionTasks getCollectionTasks(String id) {
        return collectionTasks.stream()
                .filter(o -> o.id.equals(id))
                .findAny()
                .orElse(null);
    }
   
}