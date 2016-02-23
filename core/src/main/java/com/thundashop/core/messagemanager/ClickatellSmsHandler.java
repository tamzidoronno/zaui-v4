/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.Database;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;

/**
 *
 * @author ktonder
 */
public class ClickatellSmsHandler extends SmsHandlerAbstract implements SmsHandler {

    public ClickatellSmsHandler(String storeId, Database database, String prefix, String from, String to, String message, boolean productionMode) {
        super(storeId, database, prefix, from, to, message, productionMode);
    }

    
    @Override
    public String getName() {
        return "clickatell";
    }

    @Override
    public void postSms() throws Exception {
        
        String to = getTo();
        if (to != null && to.length() > 1 && to.startsWith("0")) {
            to = to.substring(1);
        }
        
        to = to.replace("+", "");
        
        String message = URLEncoder.encode(getMessage(), "ISO-8859-1");
        String urlString = "http://api.clickatell.com/http/sendmsg?user=boggibill&password=WhatEverMan1&api_id=3588632&concat=3&to="+getPrefix()+to+"&"+"&text="+message;
      
        URL url = new URL(urlString);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String content = "";
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            content += inputLine;
        in.close();

        if(!content.trim().startsWith("ID:")) {
            logMessage("failed", content);
        }
        
        logMessage("delivered", content);
    }
}