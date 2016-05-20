/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.webmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Component;

/**
 *
 * @author hung
 */

@Component
@GetShopSession
public class WebManager extends ManagerBase implements IWebManager {
    
    private final String USER_AGENT = "Mozilla/5.0";
    
    @Override
    public String htmlGet(String url) throws Exception {
        
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        
        BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));    
        
        String responseLine;
        StringBuilder responseBuffer = new StringBuilder();
        
        while((responseLine = responseStream.readLine()) != null) {
            responseBuffer.append(responseLine);
        }
        
        return responseBuffer.toString();
    }
}
