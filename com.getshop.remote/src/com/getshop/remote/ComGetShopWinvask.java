package com.getshop.remote;

import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.Address;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ComGetShopWinvask {
    private List<String> completedProducts = new ArrayList();
    private Runner runner;

    public ComGetShopWinvask(Runner runner) {
        this.runner = runner;
    }
    
    

    public void startClient() throws Exception {
        loadCompletedProducts();
        
        while (true) {
            checkForOrders();
            Thread.sleep(5000);
        }
    }

    private void checkForOrders() throws Exception {
        List<Order> orders = this.runner.api.getOrderManager().getOrders(new ArrayList(), 0, 100000);
        for (Order order : orders) {
            if (order.status == Order.Status.WAITING_FOR_PAYMENT) {
                exportOrder(order);
            }
        }
    }
    
    private void exportOrder(Order order) throws IOException, SQLException, ClassNotFoundException {
        if (addToUpdated(order)) {
            System.out.println("This order need to be exported: " + order.id);
            WinVaskDBIntegration dbint = new WinVaskDBIntegration();
            Integer kundenr = dbint.findCustomer(order.cart.address.fullName);
            if(kundenr == -1) {
                Address address = order.cart.address;
                kundenr = dbint.createCustomer(address.fullName, address.address, address.postCode, address.city, address.phone, address.emailAddress);
            }
            System.out.println("Kundenr: " + kundenr);
            dbint.createInvoice(kundenr, order);
        }
    }

    private boolean addToUpdated(Order order) throws IOException {
        if (completedProducts.contains(order.id)) {
            return false;
        }
        completedProducts.add(order.id);
        appendToFile(order.id, "completedProducts.txt");
        
        return true; 
   }

    private void loadCompletedProducts() {
        try {
            FileInputStream fstream = new FileInputStream("completedProducts.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                completedProducts.add(strLine);
            }
        } catch (Exception e) {
        }
    }

    private void appendToFile(String line, String file) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        out.println(line);
        out.close();
    }  
}
