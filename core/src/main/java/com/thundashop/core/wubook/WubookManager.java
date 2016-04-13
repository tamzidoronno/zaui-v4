package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.productmanager.data.Product;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@GetShopSession
public class WubookManager implements IWubookManager {
    
    private XmlRpcClient client;
    String token = "";
    private String lcode = "";
    
    @Autowired
    PmsManager pmsManager;
    
    private void updateAvailability() throws Exception {
        if(!isWubookActive()) { return; }

        //Hashtable struct
        //Vectory array

        Vector<Hashtable> tosend = new Vector();

        List<String> products = new ArrayList();
        for (String pid : products) {
            Hashtable roomToUpdate = new Hashtable();

            Date date = new Date();
            long start = date.getTime() / 1000;
            long end = start + 86400;


            Vector days = new Vector();
            for (int i = 0; i < 365; i++) {
                Integer count = 0;

                Hashtable result = new Hashtable();
                result.put("avail", count);
                result.put("no_ota", 0);
                days.add(result);
                start = start + 86400;
                end = end + 86400;
            }
            roomToUpdate.put("days", days);
            tosend.add(roomToUpdate);
        }

        String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String todayString = format.format(new Date());

        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(todayString);
        params.addElement(tosend);

        Vector result = (Vector) client.execute("update_rooms_values", params);

        if (!result.get(1).equals("Ok")) {
            System.out.println("Failed to update availability, send mail about it.");
            System.out.println("0:" + result.get(0));
            System.out.println("1:" + result.get(1));
            throw new Exception();
        }
    }
    
    private boolean isWubookActive() {
        if(pmsManager.getConfigurationSecure().wubookusername == null || pmsManager.getConfigurationSecure().wubookusername.isEmpty()) {
            return false;
        }
        return true;
    }
    
    
    private boolean connectToApi() throws Exception {
        if(!isWubookActive()) { return false; }
        client = new XmlRpcClient("https://wubook.net/xrws/");
        
        Vector<String> params = new Vector<String>();
        params.addElement(pmsManager.getConfigurationSecure().wubookusername);
        params.addElement(pmsManager.getConfigurationSecure().wubookpassword);
        params.addElement(pmsManager.getConfigurationSecure().wubookproviderkey);
        Vector result = (Vector) client.execute("acquire_token", params);
        Integer response = (Integer) result.get(0);
        token = (String) result.get(1);
        if (response == 0) {
            return true;
        }
        return false;
    }
    
    private void setNoShow(String rcode) throws XmlRpcException, IOException {
        if(!isWubookActive()) { return; }
        System.out.println("Setting no show on rcode: " + rcode);
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(pmsManager.getConfigurationSecure().wubooklcode);
        params.addElement(rcode);

        client.execute("bcom_notify_noshow", params);
    }
}
