/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.sedox.magentoapi.SedoxApiPort;
import com.thundashop.core.sedox.magentoapi.SedoxApiServiceLocator;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class SedoxMagentoIntegration {

    public String login(String emailAddress, String password) {
        try {
            SedoxApiServiceLocator locator = new SedoxApiServiceLocator();
            SedoxApiPort port = locator.getSedoxApiPort();
            return port.checkLogin(emailAddress, password).toString();
        } catch (Exception ex) {
            return null;
        }

    }

    public static class Order {

        public Integer orderId;
        public String customer_id;
        public Integer credit;

        @Override
        public String toString() {
            return "orderid: " + orderId + ", customerId: " + customer_id + ", credit: " + credit;
        }
    }

    public static class MagentoUser {
        public String name;
        public String emailAddress;
        public String phone;
    }
    private static String code = "asdfae4r209345ui1ojt1jkl3541iou45h12k34jh12kl5jh36kl1h346kl1j346h134789hasdihASKDFJQWKERv89ah123NEøæå";
    private SedoxProductManager listener;

    void addOrderUpdateListener(SedoxProductManager aThis) {
        this.listener = aThis;
    }

    public MagentoUser getUserInformation(int userId) {
        try {
            SedoxApiServiceLocator locator = new SedoxApiServiceLocator();
            SedoxApiPort port = locator.getSedoxApiPort();
            HashMap<String, String> result = (HashMap) port.getUserData(code, userId);

            MagentoUser magentoUser = new MagentoUser();
            magentoUser.name = result.get("firstname") + " " + result.get("lastname");
            Object phoneObject = port.getPhoneNumber(code, userId);
            if (phoneObject != null) {
                magentoUser.phone = phoneObject.toString();
            }
            magentoUser.emailAddress = result.get("email");
            return magentoUser;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws SOAPException, ServiceException, RemoteException {
        SedoxMagentoIntegration inte = new SedoxMagentoIntegration();
        List<Order> orders = inte.getOrders();
        for (Order order : orders) {
            System.out.println(order);
        }

        inte.getUserInformation(3611);
    }

    public List<SedoxMagentoIntegration.Order> getOrders() {
        List<Order> retOrders = new ArrayList<>();

        try {
            SedoxApiServiceLocator locator = new SedoxApiServiceLocator();
            SedoxApiPort port = locator.getSedoxApiPort();


            HashMap<Integer, HashMap<String, Integer>> orders = (HashMap) port.getOrders(code);
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

    @Scheduled(fixedDelay = 60000)
    public void test() throws ErrorException {
        if (listener != null) {
            listener.updateOrders(getOrders());
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateAll() {
        if (listener != null) {
            listener.updateAllUsers();
        }
    }
}
