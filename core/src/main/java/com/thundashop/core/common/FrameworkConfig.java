/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 *
 * @author ktonder
 */
@Component
public class FrameworkConfig {

    public boolean productionMode = false;
    private String storeCreationIP = "";

    @PostConstruct
    public void readConfig() {
        BufferedReader br = null;
        String line = "";
        
        File f = new File("config.txt");
        if (!f.exists()) {
            
            f = new File("../config.txt");
            
            if (!f.exists()) {
                GetShopLogHandler.logPrintStatic("WARNING: Did not find framework config file (config.txt or ../config.txt), using default configs", null);
                return;
            }
        }
        
        try {    
            br = new BufferedReader(new FileReader(f));
            try {
                while ((line = br.readLine()) != null) {
                    String[] content = line.split(",");
                    setVariables(content);
                }
            } finally {
                br.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(FrameworkConfig.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(FrameworkConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setVariables(String[] content) {
        if (content[0].toLowerCase().equals("productionmode")) {
            productionMode = content[1].toLowerCase().equals("true");
        } else if (equalsIgnoreCase(content[0], "storeCreationIP")) {
            storeCreationIP = content[1].trim();
        }
    }

    public String getStoreCreationIP() {
        return storeCreationIP;
    }
}
