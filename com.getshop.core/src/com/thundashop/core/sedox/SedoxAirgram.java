/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.ErrorException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SedoxAirgram {
    private String sendurl = "https://api.pushover.net/1/messages.json";
    private String applicationToken = "agzzgGjfWKovkVCvGQrhjQxNNc7P5n";
    
    public void sendMessage(String pushoverUserId, String message) throws ErrorException {
        String data = "token="+applicationToken+"&user=" + pushoverUserId + "&message=" + message;
        sendToAirgramHttp(data);
    }
    
    private void sendToAirgramHttp(String data) throws ErrorException {
        try {

            URL obj = new URL(sendurl);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(data);
            out.close();
            
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
