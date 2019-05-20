/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class GetShopSchedulerOnOffHandler {

    private static GetShopSchedulerOnOffHandler handler;

    public GetShopSchedulerOnOffHandler() {
        clearState();
    }

    public static GetShopSchedulerOnOffHandler getOnOffHandler() {
        if (handler == null) {
            handler = new GetShopSchedulerOnOffHandler();
        }

        return handler;
    }

    public synchronized boolean isActive(GetShopSchedulerBase base) {
        List<String> lines = readFileIntoList();
        
        for (String line : lines) {
            String[] lineArr = line.split(";");
            if (lineArr.length == 2 && lineArr[0].equals(base.getClass().getCanonicalName())) {
                return !lineArr[1].equals("off");
            }
        }
        
        lines.add(base.getClass().getCanonicalName()+";"+"on");
        
        try {
            writeLines(lines);
        } catch (IOException ex) {
            Logger.getLogger(GetShopSchedulerOnOffHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }

    private List<String> readFileIntoList() {
        List<String> lines = Collections.emptyList();
        
        try {
            lines = Files.readAllLines(Paths.get("cron4jonoff"), StandardCharsets.UTF_8);
        } catch (IOException e) {
        }
        
        return lines;
    }

    private void clearState() {
        FileWriter fw = null;
        try {
            File file = new File("cron4jonoff");
            fw = new FileWriter(file, false);
            fw.flush();
        } catch (IOException ex) {
            Logger.getLogger(GetShopSchedulerOnOffHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(GetShopSchedulerOnOffHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void writeLines(List<String> lines) throws IOException {
        clearState();
        FileWriter writer = new FileWriter("cron4jonoff"); 
        for(String str : lines) {
          writer.write(str + "\n");
        }
        writer.close();
    }
}
