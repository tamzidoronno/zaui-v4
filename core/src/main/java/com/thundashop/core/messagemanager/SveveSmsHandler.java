/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.databasemanager.Database;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author ktonder
 */
public class SveveSmsHandler extends SmsHandlerAbstract implements SmsHandler {

    public SveveSmsHandler(String storeId, Database database, String prefix, String from, String to, String message, boolean productionMode) {
        super(storeId, database, prefix, from, to, message, productionMode);
    }

    @Override
    public String getName() {
        return "sveve";
    }

    @Override
    public void postSms() throws Exception {
        String to = getTo();
        
        if (to != null && to.length() > 1 && to.startsWith("0")) {
            to = to.substring(1);
        }
        
        String message = URLEncoder.encode(getMessage(), "UTF-8");
        String urlString = urlString = "https://sveve.no/SMS/SendMessage?user=getshopa&passwd=dza18&to=+"+getPrefix()+to+"&msg=" + message + "&from="+getFrom();
       
        URL url = new URL(urlString);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String content = "";
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            content += inputLine;
        in.close();

        if(!content.trim().startsWith("ID:")) {
            logMessage("failed", content);
        } else {
            logMessage("delivered", content);
        }
    }
    
}
