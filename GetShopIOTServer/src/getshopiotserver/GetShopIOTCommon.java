/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver;

import com.google.gson.Gson;
import com.thundashop.core.gsd.GetShopDeviceMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class GetShopIOTCommon extends Thread {
    GetShopIOTOperator operator = null;
    private String address = "";
    private String token = "";
    private SetupMessage setupMessage = null;
    
    public void setConfiguration(SetupMessage msg) {
        if(msg != null) {
            address = msg.address;
            token = msg.token;
        }
        this.setupMessage = msg;
    }
    
    public SetupMessage getConfigurationObject() {
        return setupMessage;
    }
    
    public void setAddress(String address) {
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getAddress() {
        return address;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setIOTOperator(GetShopIOTOperator operator) {
        this.operator = operator;
    }
    
    public GetShopIOTOperator getOperator() {
        return this.operator;
    }
    
    public void logPrint(String msg) {
        System.out.println(new Date() + " : " + msg);
    }
    public void logPrintWithoutLine(String msg) {
        System.out.print(msg);
    }
    public void logPrintException(Exception e) {
        e.printStackTrace();
    }
    
    public void sendMessage(String manager, String functionName, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        String address = getAddress();
        
        try {
            address += "/scripts/apiexport.php?method=" + functionName + "&manager=" + manager;
            
            
            Gson gson = new Gson();
            
            if(arg1 != null) { address += "&jsonarg1="+URLEncoder.encode(gson.toJson(arg1), "UTF-8"); }
            if(arg2 != null) { address += "&jsonarg2="+URLEncoder.encode(gson.toJson(arg2), "UTF-8"); }
            if(arg3 != null) { address += "&jsonarg3="+URLEncoder.encode(gson.toJson(arg3), "UTF-8"); }
            if(arg4 != null) { address += "&jsonarg4="+URLEncoder.encode(gson.toJson(arg4), "UTF-8"); }
            if(arg5 != null) { address += "&jsonarg5="+URLEncoder.encode(gson.toJson(arg5), "UTF-8"); }
            logPrint(address);
            URL url = new URL(address);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(20000);

            BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));

            String result = "";
            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                result += inputLine + "\r\n";
            }
            in.close();
        }catch(Exception ex) {
            logPrintException(ex);
        }
    }
    
    public SetupMessage getSetupMessage() {
        return setupMessage;
    }
    
    
    
}
