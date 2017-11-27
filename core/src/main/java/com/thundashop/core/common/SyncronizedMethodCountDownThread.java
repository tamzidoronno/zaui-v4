/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public class SyncronizedMethodCountDownThread extends Thread {
    private String managerName;
    private String methodName;
    private boolean terminate = false;
    private boolean warningMessagePrinted = false;
    private final String storeId;
    private final long methodThread;

    public SyncronizedMethodCountDownThread(String managerName, String methodName, String storeId, long methodThread) {
        this.managerName = managerName;
        this.methodName = methodName;
        this.storeId = storeId;
        this.methodThread = methodThread;
    }
    
    public void terminate() {
        terminate =  true;
    }

    @Override
    public void run() {
        int i = 0;
        
        for (i=0;i<60;i++) {
            try { Thread.sleep(1000); } catch (Exception ex) {}
            
            if (terminate) {
                return;
            }
        }
        
        System.out.println(new Date() + " | !!!!!!!!!!!!!!!!!! " + getDetails() + " !!!! Did not finish within 60seconds");
        System.out.println("============== DEBUG STACK, WHAT IS HANGING? ==================");
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getId() == this.methodThread) {
                for (StackTraceElement el : t.getStackTrace()) {
                    System.out.println(el.toString());
                }
            }
        }
        System.out.println("============== END WHAT IS HANGING? ==================");
        
        warningMessagePrinted = true;
    }
    
    public void printFinishedMessage() {
        if (warningMessagePrinted) {
            System.out.println(new Date() + " | Process has finished: " + getDetails());
        }
    }

    private String getDetails() {
        String retMsg = "Manager: " + managerName + ", function: " + methodName + ", storeid: " + storeId;
        return retMsg;
    }
    
    
}
