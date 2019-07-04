/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ratemanager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author boggi
 */
public class RateManagerPushBookingThread extends Thread {
    public String url;
    public String data;

    public RateManagerPushBookingThread(String url, String data) {
        this.url = url;
        this.data = data;
    }
    
    
    
    public void run() {
        try {
            String encoding = "UTF-8";

            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");

            connection.setDoOutput(true);
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.writeBytes(new String(data.getBytes(), encoding));
                outputStream.flush();
            }
            
            try {
                BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String responseLine;
                StringBuilder responseBuffer = new StringBuilder();

                while((responseLine = responseStream.readLine()) != null) {
                    responseBuffer.append(responseLine);
                }
            }catch(IOException ex) {
                BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String responseLine;
                StringBuilder responseBuffer = new StringBuilder();

                while((responseLine = responseStream.readLine()) != null) {
                    responseBuffer.append(responseLine);
                }
                String res = responseBuffer.toString();
                throw ex;
            }        
        }catch(Exception ee) {
            ee.printStackTrace();
        }
    }
}
