package com.getshop.remote;

import com.thundashop.api.managers.GetShopApi;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ComGetshopProteriasync {

    private List<String> completedProducts = new ArrayList();
    private Runner runner;

    public ComGetshopProteriasync(Runner runner) {
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
            if (order.status == Order.Status.COMPLETED) {
                exportOrder(order);
            }
        }
    }


    private void exportOrder(Order order) throws IOException {
        if (addToUpdated(order)) {
            Address theAddress = order.cart.address;
            
            String line = order.incrementOrderId + ";" + theAddress.emailAddress + ";" + theAddress.fullName + ";" + theAddress.postCode + ";" + theAddress.phone + ";" + order.userId + ";" + theAddress.address + ";" + theAddress.city;
            line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
            File folder = new File("import");
            if(!folder.exists()) {
                folder.mkdir();
            }
            File file = new File("import/import.txt");
            if(!file.exists()) {
                file.createNewFile();
            }
            appendToFile(line, "import/import.txt");
            System.out.println("Exporting order: " + order.id);
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
