/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CollectionTasks {
    public String id = UUID.randomUUID().toString();
    
    /**
     * Different types? 
     * 
     *    codmandatory, 
     *    cosmandatory, 
     *    codoptional, 
     *    cosoptional, 
     *    optional
     * 
     */
    public String type = "";
    
    public List<CollectionTask> collectionTasks = new ArrayList();
    
    /**
     * The date when it was collected, sent from the app
     */
    public Date date = null;
    
    /**
     * What the driver has entered as the adjustment
     */
    
    public Double cashAmount = 0D;
    public Double chequeAmount = 0D;
    public String chequeNumber = "";
    
    /**
     * Adjustments the driver can do.
     */
    public Double adjustedReturnCredit = 0D;
    public Double adjustment = 0D;
    public Double adjustmentPreviouseCredit = 0D;
    
}
