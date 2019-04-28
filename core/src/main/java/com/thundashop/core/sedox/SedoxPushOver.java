/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SedoxPushOver implements Runnable {
   private String sendurl = "https://api.pushover.net/1/messages.json";
    private String applicationToken = "agzzgGjfWKovkVCvGQrhjQxNNc7P5n";
    
    @Autowired
    private FrameworkConfig frameworkConfig;
    private String message;
    private String pushoverUserId;

    public SedoxPushOver() {
    }

    public SedoxPushOver(FrameworkConfig frameworkConfig, String message, String pushoverUserId) {
        this.frameworkConfig = frameworkConfig;
        this.message = message;
        this.pushoverUserId = pushoverUserId;
    }
    
    
    public void sendMessage(String pushoverUserId, String message) throws ErrorException {
        if (!frameworkConfig.productionMode) {
            GetShopLogHandler.logPrintStatic("Sending pushover message: " + message, null);
            return;
        }
        
        Thread td = new Thread(new SedoxPushOver(frameworkConfig, message, pushoverUserId));
        td.setName("Pushover message for sedox performance");
        td.start();
    }
    
    private void sendPushoverNotification() throws ErrorException {
        String data = "token="+applicationToken+"&user=" + pushoverUserId + "&message=" + message;
        
        try {

            URL obj = new URL(sendurl);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            try (OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream())) {
                out.write(data);
            }
            
            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        sendPushoverNotification();
    }
}
