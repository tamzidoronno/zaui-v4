/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.usermanager.data.User;
import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author ktonder
 */
public abstract class GetShopSchedulerBase implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(GetShopSchedulerBase.class);

    private GetShopApi api;
    private String sessionId = UUID.randomUUID().toString();
    private boolean loggedOn = false;
    private Scheduler scheduler;
    private String password;
    private String username;
    private String multiLevelName;
    private String webAddress;
    private String storeId;
    private String schedulerInterval = "";
    
    private static ConcurrentHashMap<String, ReentrantLock> storeLocks = new ConcurrentHashMap();
    
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
        this.schedulerInterval = scheduler;
        
        if(scheduler != null) {
            createScheduler(scheduler);
        }
    }

    public GetShopSchedulerBase() {
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setStoreId(String storeId) {
        this.storeId = storeId;
        setThreadName();
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
        if (this.api != null) {
            boolean isLoggedIn = this.api.getUserManager().isLoggedIn();
            if (!isLoggedIn) {
                log.info("GetShopSchedulerBase | reconnecting and logging in again : " + storeId + " | " + webAddress + " | username: " + username + " | password: " + password);
                sessionId = UUID.randomUUID().toString();
                this.api = new GetShopApi(25554, "localhost", sessionId, webAddress);
                try {
                    User user = this.api.getUserManager().logOn(username, password);
                    
                    if (user == null) {
                        log.debug("GetShopSchedulerBase | tried to login but was not able to log in again : " + storeId + " | " + webAddress);
                    } else {
                        log.debug("GetShopSchedulerBase | logged in with user on store : " + storeId + " | " + webAddress  + " | User print: " + user + " | User id: " + user.id + " | type : " + user.type );
                    }
                } catch (Exception ex) {
                    log.error("Failed to login with exception storeId `{}`, webAddress `{}`", storeId, webAddress, ex);
                }
            }
        }
        
        if (!this.loggedOn || this.api == null) {
            this.api = new GetShopApi(25554, "localhost", sessionId, webAddress);
            User user = this.api.getUserManager().logOn(username, password);
            this.loggedOn = true;
            if (user == null) {
                log.debug("GetShopSchedulerBase | not able to login? why? : " + storeId + " | " + webAddress);
            }
        }
    
        return this.api;
    }
    
    public abstract void execute() throws Exception;

    private void createScheduler(String scheduler) {
        this.scheduler = new Scheduler();
        this.scheduler.schedule(scheduler, this);
        this.scheduler.start();
        
        setThreadName();
    }
    
    private synchronized void runConnection() throws Exception {
        if (!GetShopSchedulerOnOffHandler.getOnOffHandler().isActive(this)) {
            return;
        }
        
        sleepRandomTime();
        
        ReentrantLock storeLock = getStoreLock();
        
        if (storeLock != null) {
            storeLock.lock();
        }
        
        try {
            execute();
            closeConnection();
            
        } catch (Exception ex) {
            log.error("", ex);
        }
        
        if (storeLock != null) {
            storeLock.unlock();
        }
    }
    
    @Override
    public void run() {
        try {
            runConnection();
        } catch (Exception ex) {
            log.error("Problem with scheduled task....", ex);
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
                log.error("Error while closing connection", ex);
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
    
    public String getStoreId() {
        return storeId;
    }

    private void setThreadName() {
        if (this.scheduler == null) {
            return;
        }
        
        try {
            Field field = this.scheduler.getClass().getDeclaredField("timer");
            field.setAccessible(true);
            Thread value = (Thread) field.get(this.scheduler);
            value.setName("cron4j::" + this.getClass().getCanonicalName()  + "::" + schedulerInterval + "::" + storeId + "::" + this.scheduler.getGuid() );
        } catch (Exception ex) {
            log.error("", ex);
        } 
    }

    private ReentrantLock getStoreLock() {
        if (storeId == null || storeId.isEmpty()) 
            return null;
        
        ReentrantLock lock = storeLocks.get(storeId);
        
        if (lock == null) {
            lock = new ReentrantLock();
            storeLocks.put(storeId, lock);
        }
        
        return lock;
    }

}
