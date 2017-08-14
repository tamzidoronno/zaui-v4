/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.usermanager.data.User;
import it.sauronsoftware.cron4j.Scheduler;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public abstract class GetShopSchedulerBase implements Runnable {
    private GetShopApi api;
    private final String sessionId = UUID.randomUUID().toString();
    private boolean loggedOn = false;
    private Scheduler scheduler;
    private String password;
    private String username;
    private String multiLevelName;
    private String webAddress;
    
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    
    public GetShopSchedulerBase(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        this.username = username;
        this.password = password;
        this.webAddress = webAddress;
        this.multiLevelName = multiLevelName;
        
        if(scheduler != null) {
            createScheduler(scheduler);
        }
    }

    public GetShopSchedulerBase() {
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMultiLevelName(String multiLevelName) {
        this.multiLevelName = multiLevelName;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }
    
    protected GetShopApi getApi() throws Exception {
        if (!this.loggedOn || this.api == null) {
            this.api = new GetShopApi(25554, "localhost", sessionId, webAddress);
            User user = this.api.getUserManager().logOn(username, password);
            this.loggedOn = true;
        }
    
        return this.api;
    }
    
    public abstract void execute() throws Exception;

    private void createScheduler(String scheduler) {
        this.scheduler = new Scheduler();
        this.scheduler.schedule(scheduler, this);
        this.scheduler.start();
    }
    
    private synchronized void runConnection() throws Exception {
        sleepRandomTime();
        execute();
        closeConnection();
    }
    
    @Override
    public void run() {
        try {
            runConnection();
        } catch (Exception ex) {
            GetShopLogHandler.logPrintStatic("Problem with scheduled task....", null);
            ex.printStackTrace();
        }
    }

    public void stop() {
        try {
            this.scheduler.stop();
        } catch (IllegalStateException ex) {
            // Already stopped
        }
    }
    
    public String getMultiLevelName() {
        return multiLevelName;
    }

    private void closeConnection() {
        if (this.api != null) {
            try {
                this.loggedOn = false;
                this.api.getUserManager().logout();
                this.api.transport.close();
                this.api = null;
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(GetShopSchedulerBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sleepRandomTime() {
        Random r = new Random();
        int Low = 0;
        int High = 5000;
        int Result = r.nextInt(High-Low) + Low;
        
        try { Thread.sleep(Result); } catch (Exception ex) {}
    }
}
