/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.printmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */

/**
 * Warning, this manager is used trought all stores.
 * @author ktonder
 */
@Component
public class PrintManager extends ManagerBase implements IPrintManager {

    /**
    * The printer ids needs to be unique!
    */
    public List<PrintJob> printJobs = new ArrayList();
    
    @Override
    public synchronized List<PrintJob> getPrintJobs(String printerId) {
        List<PrintJob> toReturn = printJobs.stream()
                .filter(job -> job.printerId.equals(printerId))
                .collect(Collectors.toList());
        
        printJobs.removeAll(toReturn);
        
        return toReturn;
    }
    
    public synchronized void addPrintJob(PrintJob printJob) {
        printJobs.add(printJob);
    }
    
    /*
    TODO - MAKE A CLEANUP FOR PROCESSES THAT 
    HAS NOT BEEN PICKED UP!
    WE DONT WANT TO FILL UP THE QUEUE.
    */
    private void cleanup() {
        
    }
}