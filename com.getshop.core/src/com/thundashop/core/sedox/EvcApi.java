/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.FrameworkConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class EvcApi {
    public String address = "https://evc.de/services/api_resellercredits.asp";
    public String userName = "12195";
    public String password = "l37UOR8Zig51PeRd";
    public String apiid = "j34sbc93hb90";
    
    @Autowired
    private FrameworkConfig frameworkConfig;
    
    private String request(String verb) {
        BufferedReader in = null;
        try {
            String result = "";
            String url = address+"?apiid="+apiid+"&username="+userName+"&password="+password+"&verb="+verb;
            URL yahoo = new URL(url);
            URLConnection yc = yahoo.openConnection();
            in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result += inputLine;
            in.close();
            
            return result;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        return null;
    }
    
    public boolean isPersonalAccount(String evcId) {
        String result = request("getcustomeraccount&customer=" + evcId );
        
        if (result != null && result.subSequence(0, 2).equals("ok")) 
            return true;
        
        
        return false;
    }
    
    public boolean addPersonalAccount(String evcId) {
        String result = request("addcustomer&customer=" + evcId );
        
        if (result != null && result.subSequence(0, 2).equals("ok")) 
            return true;
        
        return false;
    }
    
    public int getPersonalAccountBalance(String evcId) {
        String result = request("getcustomeraccount&customer=" + evcId);
        
        if (result != null && result.subSequence(0, 2).equals("ok")) 
            return Integer.valueOf(result.substring(3).trim());
        
        return 0;
    }
    
    public boolean setPersonalAccountBalance(String evcId, int newBalance) {
        if (!frameworkConfig.productionMode) {
            return true;
        }
        
        String result = request("setcustomeraccount&customer=" + evcId+"&credits="+newBalance);

        if (result != null && result.subSequence(0, 2).equals("ok")) 
            return true;
        
        return false;
    }
    
}
