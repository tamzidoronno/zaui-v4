/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class GetShopProfiler implements Runnable {
    private final BlockingQueue<String> toLog;
    private String fileName;

    public GetShopProfiler() {
        this.toLog = new LinkedBlockingQueue();
        
        try {
            createLogFile();
        } catch (IOException ex) {
            Logger.getLogger(GetShopProfiler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        new Thread(this).start();
    }
    
    public void addToProfiler(String storeId, String className, String methodName, long nanosecondsUsed) {
        this.toLog.add(storeId +";"+className+";"+methodName+";"+nanosecondsUsed+";"+System.currentTimeMillis());
    }

    @Override
    public void run() {
        while(true) {
            try {
                long sleepTime = 1000 * 60 * 5;
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                return;
            }
            
            long start = System.currentTimeMillis();
            Collection<String> set = new TreeSet();
            
            if (toLog.isEmpty()) {
                continue;
            }
            
            try {
                List<String> lines = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
                set.addAll(lines);
                File logFileName = new File(fileName);
                logFileName.delete();
                createLogFile();
            } catch (IOException ex) {
                Logger.getLogger(GetShopProfiler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            toLog.drainTo(set);
            
            set.stream().forEach(o -> {
                try {
                    writeContent(o);
                } catch (IOException ex) {
                    Logger.getLogger(GetShopProfiler.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            System.out.println("Handled profiling in " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    private void writeContent(String o) throws IOException {
        String[] args = o.split(";");
        
        if (args.length < 4) {
            return;
        }
        
        long addedTimeStamp = Long.parseLong(args[4]);
        long timeSinceAdded = System.currentTimeMillis() - addedTimeStamp;
        
        // Log for 10 minutes just to start testing with.
        long timeToLive = 1000 * 60 * 10;
        
        if (timeSinceAdded > timeToLive) {
            return;
        }
        
        String logLine = o + "\n";
        Files.write(Paths.get(fileName), logLine.getBytes(), StandardOpenOption.APPEND);
    }

    private void createLogFile() throws IOException {
        fileName = "log/getshop_profiler.db";

        File logFileName = new File(fileName);
        if (!logFileName.exists()) {
            logFileName.createNewFile(); 
        }
    }
}
