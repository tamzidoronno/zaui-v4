/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

import com.getshop.scope.GetShopSchedulerBase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thundashop.core.getshop.data.GetShopDevice;
import com.thundashop.core.pmsmanager.PmsLockServer;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.joda.time.Instant;


/**
 *
 * @author ktonder
 */
public class GetShopLockRazberryLogFetcher extends GetShopSchedulerBase {
    private PmsLockServer pmsLockServer;
    private GetShopDevice device;

    public GetShopLockRazberryLogFetcher(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    public GetShopLockRazberryLogFetcher(PmsLockServer pmsLockServer, GetShopDevice device) {
        this.pmsLockServer = pmsLockServer;
        this.device = device;
    }
    
    @Override
    public void execute() throws Exception {
        waitAMinute();
        List<RazberryLockLoggingResult> results = fetchResultFromServer();
        
        List<GetShopDeviceLog> logs = results.stream()
                .map(o -> convertToGetShopLog(o))
                .collect(Collectors.toList());
                
        getApi().getGetShopLockManager().addLockLogs(getMultiLevelName(), logs, "asdfi0u23l4knraslnjdfakjwenrlikqnfklasdfbaewjhbq2hjb4rl1khb34r12lh34");
    }

    private GetShopDeviceLog convertToGetShopLog(RazberryLockLoggingResult result) {
        GetShopDeviceLog log = new GetShopDeviceLog();
        log.deviceId = device.zwaveid;
        log.serverSource = pmsLockServer.serverSource;
       
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d, hh:mm:ss");
        Date date = null;
        
        try {
            date = formatter.parse(result.time.value);
        } catch (ParseException ex) {
            Logger.getLogger(GetShopLockRazberryLogFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        log.timestamp = date;
        log.uId = result.uId.value;
        
        return log;
    }
    
    private List<RazberryLockLoggingResult> fetchResultFromServer() {
        List<RazberryLockLoggingResult> results = new ArrayList();
        
        try {
            String postfix = "ZWave.zway/Run/devices["+device.zwaveid+"].instances[0].commandClasses[76].data";
            postfix = URLEncoder.encode(postfix, "UTF-8");
            String address = "http://"+pmsLockServer.arxHostname+":8083/" + postfix;
            String res = GetshopLockCom.httpLoginRequest(address,pmsLockServer.arxUsername,pmsLockServer.arxPassword);
  
            Gson gson = new Gson();
            JsonElement element = gson.fromJson(res, JsonElement.class);

            if (!element.isJsonObject()) {
                throw new RuntimeException("Could not fetch result from razberry server");
            }

            JsonObject jsonObj = element.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
                JsonElement innerElement = entry.getValue();
                if (!innerElement.isJsonObject()) {
                    continue;
                }

                if (innerElement.getAsJsonObject().get("uId") != null) {
                    RazberryLockLoggingResult log = gson.fromJson(innerElement, RazberryLockLoggingResult.class);
                    results.add(log);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList();
        }
        return results;
    }

    // We wait a bit for the server to retreive all the latest codes.
    private void waitAMinute() {
        try { Thread.sleep(60000); } catch (Exception ex) {};
    }
    
}