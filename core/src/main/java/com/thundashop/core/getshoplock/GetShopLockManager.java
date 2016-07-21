/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import static com.thundashop.core.arx.DoorManager.wrapClient;
import com.thundashop.core.getshop.data.GetShopDevice;
import com.thundashop.core.getshop.data.ZWaveDevice;
import com.thundashop.core.pmsmanager.PmsManager;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component 
@GetShopSession
public class GetShopLockManager extends GetShopSessionBeanNamed implements IGetShopLockManager {
    private HashMap<String, GetShopDevice> devices = new HashMap();
    
    @Autowired
    PmsManager pmsManager;
    
    public String getUsername() {
        return pmsManager.getConfigurationSecure().arxUsername;
    }
    
    public String getHostname() {
        return pmsManager.getConfigurationSecure().arxHostname;
    }
    
    public String getPassword() {
        return pmsManager.getConfigurationSecure().arxPassword;
    }
    
    private String httpLoginRequest(String address) throws Exception {

        String loginToken = null;
        String loginUrl = address;
        
        HttpParams my_httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, 3000);
        HttpConnectionParams.setSoTimeout(my_httpParams, 6000);
        
        DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
        client = wrapClient(client);
        HttpResponse httpResponse;
        

        HttpEntity entity;
        HttpGet request = new HttpGet(loginUrl);
        byte[] bytes = (getUsername() + ":" + getPassword()).getBytes();
        String encoding = Base64.encode(bytes);

        request.addHeader("Authorization", "Basic " + encoding);

        System.out.println("Now sending to arx");
        httpResponse = client.execute(request);

        Integer statusCode = httpResponse.getStatusLine().getStatusCode();
        if(statusCode == 401) {
            return "401";
        }


        entity = httpResponse.getEntity();



        System.out.println("Done sending to arx");

        if (entity != null) {
            InputStream instream = entity.getContent();
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = instream.read()) != -1) {
                sb.append((char) ch);
            }
            String result = sb.toString();
            return result.trim();
        }
            
        return "failed";
    }

    public String pushCode(String id, String door, String code, Date start, Date end) throws Exception {
        SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.YYYY HH:mm");
        String startString = start.getTime()/1000 + "";
        String endString = end.getTime()/1000 + "";
        
        id = URLEncoder.encode(id, "UTF-8");
        
        String address = "http://"+getHostname()+":8080/storecode/"+door+"/"+id+"/"+startString+"/"+endString+"/" + code;
        System.out.println("Executing: " + address);
        return this.httpLoginRequest(address);
    }

    public String removeCode(String pmsBookingRoomId) throws Exception {
        String id = URLEncoder.encode(pmsBookingRoomId, "UTF-8");
        String address = "http://"+getHostname()+":8080/deletekey/"+id;
        System.out.println("Executing: " + address);
        return this.httpLoginRequest(address);
    }

    @Override
    public String getCodeForLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<GetShopDevice> getAllLocks() {
        String hostname = getHostname();
        if(hostname == null || hostname.isEmpty()) { return new ArrayList(); }
        try {
            String address = "http://" + hostname + ":8083/ZWave.zway/Run/devices";
            System.out.println(address);
            String res = httpLoginRequest(address);
            
            HashMap<Integer, ZWaveDevice> result = new HashMap();
            Type type = new TypeToken<HashMap<Integer, ZWaveDevice>>(){}.getType();
            Gson gson = new Gson();
            result = gson.fromJson(res, type);
            
            for(Integer offset : result.keySet()) {
                ZWaveDevice device = result.get(offset);
                System.out.println("ID: " + device.id + " - " + device.data.deviceTypeString.value);
                GetShopDevice gsdevice = new GetShopDevice(device);
                addDeviceIfNotExists(gsdevice);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList(devices.values());
    }

    @Override
    public void openLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean pingLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeCodeOnLock(String lockId, String code) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void checkIfAllIsOk() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteLock(String code, String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMasterCode(Integer slot, String code) {
        if(slot > 5) {
            System.out.println("Only slot 0 to 5 is reserved for mastercodes");
        }
        
    }

    private void addDeviceIfNotExists(GetShopDevice gsdevice) {
        for(GetShopDevice dev : devices.values()) {
            if(dev.zwaveid.equals(gsdevice.zwaveid)) {
                return;
            }
        }
        
        saveObject(gsdevice);
        devices.put(gsdevice.id, gsdevice);
    }
    
}
