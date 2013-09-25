package com.getshop.proteriasync;

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

    GetShopApi api = null;
    private String sessid;
    private List<String> completedProducts = new ArrayList();

    public static void main(String[] args) throws Exception {
        ComGetshopProteriasync runner = new ComGetshopProteriasync();
        runner.startClient();
    }
    private String address = null;
    private String username = null;
    private String password = null;

    private void startClient() throws Exception {
        sessid = UUID.randomUUID().toString();
        loadCompletedProducts();
        connectToBackend();

        while (true) {
            checkForOrders();
            Thread.sleep(5000);
        }
    }

    private void checkForOrders() throws Exception {
        List<Order> orders = api.getOrderManager().getOrders(new ArrayList(), 0, 100000);
        System.out.println(orders.size());
        for (Order order : orders) {
            if (order.status == Order.Status.COMPLETED) {
                exportOrder(order);
            }
        }
    }

    public void writeConfiguration() {
        try {
            // Create file 
            FileWriter fstream = new FileWriter("config.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(address + "\n");
            out.write(username + "\n");
            out.write(password + "\n");
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void readConfiguration() {
        try {
            FileInputStream fstream = new FileInputStream("config.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            address = br.readLine();
            username = br.readLine();
            password = br.readLine();
            in.close();
        } catch (Exception e) {
            return;
        }
    }

    private void connectToBackend() throws Exception {
        readConfiguration();
        if (address == null || username == null || password == null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Store address: ");
            address = br.readLine();
            System.out.print("Username / email: ");
            username = br.readLine();
            System.out.print("Password: ");
            password = br.readLine();
        }

        String serveraddr = "backend.getshop.com";
        System.out.println("Connecting to : " + serveraddr + " port : " + 25554);
        api = new GetShopApi(25554, serveraddr, sessid, address);
        try {
            User result = api.getUserManager().logOn(username, password);
            if (!api.getUserManager().isLoggedIn()) {
                System.out.println("Failed to logon to backend");
                System.exit(0);
            }
            System.out.println("Connected to backend");
            writeConfiguration();
        } catch (Exception e) {
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
