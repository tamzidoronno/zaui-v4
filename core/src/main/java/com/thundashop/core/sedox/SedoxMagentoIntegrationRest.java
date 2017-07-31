/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.sedox.magentoapi.SedoxApiPort;
import com.thundashop.core.sedox.magentoapi.SedoxApiServiceLocator;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SedoxMagentoIntegrationRest {
    private String address = "http://www.tuningfiles.com/sedoxapi/api-rest.php";
    private Gson gson = new Gson();
    
    public String login(String emailAddress, String password) {
        try {
            String content = fetchContent("checkLogin", "&username="+emailAddress+"&password="+password);
            return gson.fromJson(content, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    private String getGroup(String groupId) {

        String groupName = "";
        if (groupId != null && !groupId.isEmpty()) {
            groupName = groupId.equals("1") ?  "General	General Customer" : groupName;
            groupName = groupId.equals("0") ?  "NOT LOGGED IN" : groupName;
            groupName = groupId.equals("3") ?  "Sedox 100" : groupName;
            groupName = groupId.equals("2") ?  "Sedox 120" : groupName;
            groupName = groupId.equals("31") ?  "Sedox 150" : groupName;
            groupName = groupId.equals("33") ?  "Sedox 50" : groupName;
            groupName = groupId.equals("4") ?  "Sedox 70" : groupName;
            groupName = groupId.equals("34") ?  "Sedox 80 (10×70)" : groupName;
            groupName = groupId.equals("37") ?  "Sedox NO 120" : groupName;
            groupName = groupId.equals("35") ?  "Sedox NO 200" : groupName;
            groupName = groupId.equals("39") ?  "Sedox NO 70" : groupName;
            groupName = groupId.equals("40") ?  "Subdealer 150" : groupName;
        }
        
        return groupName;
    }

    List<String> getAllCustomersNotExists(UserManager userManager) {
        List<String> retValues = new ArrayList();
        
        try {
            String text = getText( "http://www.tuningfiles.com/sedoxapi/listCustomers.php?code=asdfae4r209345ui1ojt1jkl3541iou45h12k34jh12kl5jh36kl1h346kl1j346h134789hasdihASKDFJQWKERv89ah123NE%C3%B8%C3%A6%C3%A5" );
            Gson gson = new Gson();
            List<String> customers = gson.fromJson(text, List.class);
            for (String cust : customers) {
                User user = userManager.getUserById(cust);
                if (user == null) {
                    retValues.add(cust);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SedoxMagentoIntegrationRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return retValues;
    }
    
    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    private String fetchContent(String checkLogin, String string) {
        try {
            String out = new Scanner(new URL(address + "?call=" + checkLogin + string).openStream(), "UTF-8").useDelimiter("\\A").next();
            return out;
        } catch (MalformedURLException ex) {
            Logger.getLogger(SedoxMagentoIntegrationRest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SedoxMagentoIntegrationRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

    public static class Order {

        public Integer orderId;
        public String customer_id;
        public Integer credit;
        public String evccustomerid;

        @Override
        public String toString() {
            return "orderid: " + orderId + ", customerId: " + customer_id + ", credit: " + credit + ", evc:  " + evccustomerid;
        }
    }

    public static class MagentoUserOriginal {
        private String firstname;
        private String lastname;
        private String group_id;
        private String email;
        private String companyName;
    }
    
    public static class MagentoUser {
        public String name;
        public String emailAddress;
        public String phone;
        public String group;
        public String companyName = "";
    }
    
    private static String code = "asdfae4r209345ui1ojt1jkl3541iou45h12k34jh12kl5jh36kl1h346kl1j346h134789hasdihASKDFJQWKERv89ah123NEøæå";


    public MagentoUser getUserInformation(int userId) {
        try { 
            String content = fetchContent("getUserInformation", "&code="+code+"&userId="+userId);
            MagentoUserOriginal original = gson.fromJson(content, MagentoUserOriginal.class);

            MagentoUser magentoUser = new MagentoUser();
            magentoUser.name = original.firstname + " " + original.lastname;
            magentoUser.group = getGroup(original.group_id);
            
            content = fetchContent("getPhoneNumber", "&code="+code+"&userId="+userId);
            magentoUser.phone = gson.fromJson(content, String.class);
            magentoUser.emailAddress = original.email;
            magentoUser.companyName = original.companyName;
            return magentoUser;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws SOAPException, ServiceException, RemoteException {
        SedoxMagentoIntegrationRest inte = new SedoxMagentoIntegrationRest();
    }

    public List<SedoxMagentoIntegrationRest.Order> getOrders() {
        List<Order> retOrders = new ArrayList<>();

        try {
            String content = fetchContent("getOrders", "&code="+code);
            
            Type type = new TypeToken<HashMap<Integer, HashMap<String, Integer>>>(){}.getType();
            HashMap<Integer, HashMap<String, Integer>> orders = gson.fromJson(content, type);
            for (Integer ibjeckey : orders.keySet()) {
                HashMap ibjec = orders.get(ibjeckey);

                Order order = new Order();
                order.orderId = ibjeckey;

                for (Object keysd : ibjec.keySet()) {
                    Object val = ibjec.get(keysd);
                    if (val instanceof Integer) {
                        Integer ivalue = (Integer) val;
                        order.credit = ivalue;
                    }
                    if (val instanceof String) {
                        String svalue = (String) val;
                        order.customer_id = svalue;
                    }
                }
                retOrders.add(order);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retOrders;
    }
    
    public List<SedoxMagentoIntegrationRest.Order> getEvcOrders() {
        List<Order> retOrders = new ArrayList<>();

        try {
            
            String content = fetchContent("getEvcOrders", "&code="+code);
            
            Type type = new TypeToken<HashMap<Integer, HashMap<String, Integer>>>(){}.getType();
            HashMap<Integer, HashMap<String, Integer>> orders = gson.fromJson(content, type);
            
            for (Integer ibjeckey : orders.keySet()) {
                HashMap ibjec = orders.get(ibjeckey);

                Order order = new Order();
                order.orderId = ibjeckey;

                for (Object keysd : ibjec.keySet()) {
                    Object val = ibjec.get(keysd);
                    if (val instanceof Integer && keysd.equals("credit")) {
                        Integer ivalue = (Integer) val;
                        order.credit = ivalue;
                    }
                    if (val instanceof String && keysd.equals("evccustomerid")) {
                        String svalue = (String) val;
                        order.evccustomerid = svalue;
                    }
                    if (val instanceof String && keysd.equals("customer_id")) {
                        String svalue = (String) val;
                        order.customer_id = svalue;
                    }
                }
                retOrders.add(order);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retOrders;
    }

}
