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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import lombok.Getter;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 * @author ktonder
 */
@Component
public class FrameworkConfig {

    public boolean productionMode = false;
    private String storeCreationIP = "";
    @Getter
    private String gotoCancellationEndpoint = "";
    @Getter
    private String gotoCancellationAuthKey = "";

    @PostConstruct
    public void readConfig() {
        File f = getConfigFile();
        if(f == null) return;
        Map<String, String> configValues = getConfigValues(f);
        setVariables(configValues);
    }

    private File getConfigFile() {
        File f = new File("config.txt");
        if (!f.exists()) {

            f = new File("../config.txt");

            if (!f.exists()) {
                GetShopLogHandler.logPrintStatic("WARNING: Did not find framework config file (config.txt or ../config.txt)," +
                        " using default configs", null);
                return null;
            }
        }
        return f;
    }

    private Map<String, String> getConfigValues(File f) {
        BufferedReader br = null;
        String line;
        Map<String, String> configValues = new HashMap<>();
        try {
            br = new BufferedReader(new FileReader(f));
            try {
                while ((line = br.readLine()) != null) {
                    String[] content = line.split(",");
                    if(content.length<2 || isBlank(content[0]) || isBlank(content[1]))
                        continue;
                    configValues.put(content[0].toLowerCase(), content[1]);
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
        return configValues;
    }

    private void setVariables(Map<String, String> configValues) {
        productionMode = configValues.containsKey("productionmode") ?
                configValues.get("productionmode").equals("true") : productionMode;
        storeCreationIP = configValues.containsKey("storeCreationIP") ?
                configValues.get("storeCreationIP") : storeCreationIP;
        gotoCancellationEndpoint = configValues.containsKey("gotocancellationendpoint") ?
                configValues.get("gotocancellationendpoint") : gotoCancellationEndpoint;
        gotoCancellationAuthKey = configValues.containsKey("gotocancellationauthkey") ?
                configValues.get("gotocancellationauthkey") : gotoCancellationAuthKey;
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
