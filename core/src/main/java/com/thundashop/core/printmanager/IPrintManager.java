/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.printmanager;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopNotSynchronized;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IPrintManager {
    
    /**
     * This function is multithreaded
     * It will not cause the rest of the system to hang and 
     * is though of being called multiple times during a second.
     * 
     * Designed for invoiking 4 times a second.
     * 
     * @param printerId
     * @return 
     */
    @GetShopNotSynchronized
    public List<PrintJob> getPrintJobs(String printerId);
    
}
