/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import java.util.Date;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CollectionTask {
    public String id = UUID.randomUUID().toString();
    
    public String referenceNumber = "";
    public String podBarcode = "";
    
    public boolean isCod = false;
    public boolean isCos = false;
    public boolean isOptional = false;
    
    
    public double amount = 0D;
    
    public Double previouseCreditAmount = 0D;
    public Double previouseCreditAmountOverride = 0D;
    public boolean collectPreviouseInvoiceCredit = false;
    public String customerNumber;
    
    
    public String getCollectionType() {
        String collectionType = "unkown";
        
        if (isCod && !isOptional)
            collectionType = "codmandatory";
        
        if (isCos && !isOptional)
            collectionType ="cosmandatory";
        
        // NEVER
        if (isCos && isOptional)
            throw new RuntimeException("This should never happen");
        
        // NEVER HAPPEN
        if (isCod && isOptional)
            throw new RuntimeException("This should never happen");
        
        if (isOptional)
            collectionType ="optional"; 
        
        return collectionType;
    }
}
