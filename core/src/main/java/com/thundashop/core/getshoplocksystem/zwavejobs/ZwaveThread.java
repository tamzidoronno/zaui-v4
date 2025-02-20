/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem.zwavejobs;

import com.google.gson.Gson;
import java.util.Calendar;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.getshoplocksystem.LocstarLock;
import com.thundashop.core.getshoplocksystem.ZwaveLockServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public abstract class ZwaveThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ZwaveThread.class);

    public boolean shouldStop = false;
    public boolean successfullyCompleted = false;
    public ZwaveThreadExecption exception = null;
    public ZwaveLockServer server;
    protected final LocstarLock lock;
    private int attempts;
    public final String storeId;

    public ZwaveThread(ZwaveLockServer server, LocstarLock lock, int attempts, String storeId) {
        this.storeId =  storeId;
        this.server = server;
        this.lock = lock;
        this.attempts = attempts;
    }
    
    public String getLockId() {
        return lock.id;
    }
    
    public String getServerId() {
        return server.id;
    }

    @Override
    public void run() {
        if (GetShopLogHandler.isDeveloper) {
            return;
        }
        
        lock.lastStartedUpdating = new Date();
        lock.currentlyUpdating = true;
        lock.dead = false;
       
        try {
            sendNoOperationSignal();
        } catch (Exception ex) {
            lock.currentlyUpdating = false;
            server.threadDone(this);
            return;
        }
        
        for (int i = 0; i < attempts; i++) {
            if (shouldStop) {
                break;
            }
            
            lock.currentlyAttempt = i;

            try {
                if (i == 0) {
                    stopIfDead(i);
                }
                boolean threadSuccess = execute(i);

                if (threadSuccess) {
                    successfullyCompleted = true; 
                    break;
                }
            } catch (Exception ex) {
                if (ex instanceof ZwaveThreadExecption) {
                    server.threadFailed(this);
                } else {
                    logger.error("sid-{}", storeId, ex);
                }

                break;
            }

            try {
                Thread.sleep(2000);
            } catch (Exception ex) {
            }
        }

        lock.currentlyUpdating = false;
        server.threadDone(this);
    }

    private void stopIfDead(int attempt) throws ZwaveThreadExecption {
        if (isDeviceDead()) {
            try {Thread.sleep(30000); }  catch (Exception ex) {}
            throw new ZwaveThreadExecption("Detected dead device, moving on", attempt);
        }
    }
     
    public void stop() {
        shouldStop = true;
    }

    public abstract boolean execute(int attempt) throws ZwaveThreadExecption;

    public void waitForEmptyQueue() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 360);
        String postfix = "ZWave.zway/InspectQueue";

        while (true) {
            String res = server.httpLoginRequestZwaveServer(postfix);
            if (res.equals("[]")) {
                break;
            }

            if (isDoneProcessingQueue(res)) {
                break;
            }

            if (cal.getTime().before(new Date())) {
                throw new ZwaveThreadExecption("Queue did not empty within timeout", 1);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                logger.error("sid-{}", storeId, ex);
            }
        }
    }

    private boolean isDoneProcessingQueue(String queue) {
        Gson gson = new Gson();
        List<Object> toParse = new ArrayList();
        try {
            toParse = gson.fromJson(queue, toParse.getClass());
            for (Object a : toParse) {
                String line = a.toString();
                if (line.contains("0.20000000298023224")) {
                    continue;
                }
                String[] lineElements = line.split(",");
                if (line.length() > 8 && lineElements[5].trim().equals("1.0")) {
                    continue;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isDeviceDead() {
        String postfix = "ZWave.zway/Run/devices["+lock.zwaveDeviceId+"]";
        String res = server.httpLoginRequestZwaveServer(postfix);
        Gson gson = new Gson();
        try {
            ZwaveStatusDevice device = gson.fromJson(res, ZwaveStatusDevice.class);
            if (device == null) {
                lock.dead = true;
                lock.markedDateAtDate = new Date();
                return true;
            }
            
            boolean dead = device.data.isFailed.value;
            
            if (dead) {
                lock.dead = true;
                lock.markedDateAtDate = new Date();
            }
            
            return dead;
        } catch (Exception ex) {
            logger.error("sid-{}", storeId, ex);
        }
        
        return false;
    }

    private void sendNoOperationSignal() {
        String postfix = "ZWave.zway/Run/devices["+lock.zwaveDeviceId+"].SendNoOperation()";
        server.httpLoginRequestZwaveServer(postfix);
        waitForEmptyQueue();
    }

}
