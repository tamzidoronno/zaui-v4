/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.storemanager;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class GSEnvironments {
    public HashMap<String, GSEnvironment> environments = new HashMap();

    public GSEnvironments() {
        addEnvironment("3.0.prod", "clients.getshop.com", 4224);
        addEnvironment("3.0.local", "localhost", 25554);
    }

    
    public Map<String, GSEnvironment> getEnvironments() {
        return environments;
    }

    private void addEnvironment(String name, String host, int port) {
        GSEnvironment gsEnvironment = new GSEnvironment(port, host, name);
        environments.put(gsEnvironment.getName(), gsEnvironment);
    }

    public GSEnvironment get(String environment) {
        return environments.get(environment);
    }
    
    
}
