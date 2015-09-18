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

/**
 *
 * @author ktonder
 */
@Component
public class FrameworkConfig {

    public boolean productionMode = false;

    @PostConstruct
    public void readConfig() {
        BufferedReader br = null;
        String line = "";
        
        File f = new File("config.txt");
        
        if (!f.exists()) {
            
            f = new File("../config.txt");
            
            if (!f.exists()) {
                System.out.println("WARNING: Did not find framework config file (config.txt or ../config.txt), using default configs");
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
        }
    }
}
