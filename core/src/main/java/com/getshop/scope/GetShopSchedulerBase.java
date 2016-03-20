/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.usermanager.data.User;
import it.sauronsoftware.cron4j.Scheduler;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public abstract class GetShopSchedulerBase implements Runnable {
    private GetShopApi api;
    private final String sessionId = UUID.randomUUID().toString();
    private boolean loggedOn = false;
    private Scheduler scheduler;
    private final String password;
    private final String username;
    private final String multiLevelName;
    private final String webAddress;
    
    public GetShopSchedulerBase(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        this.username = username;
        this.password = password;
        this.webAddress = webAddress;
        this.multiLevelName = multiLevelName;
        
        if(scheduler != null) {
            createScheduler(scheduler);
        }
    }
    
    protected GetShopApi getApi() throws Exception {
        if (!this.loggedOn) {
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
    
    @Override
    public void run() {
        try {
            execute();
        } catch (Exception ex) {
            System.out.println("Problem with scheduled task....");
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
}
