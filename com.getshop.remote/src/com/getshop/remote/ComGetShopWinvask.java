package com.getshop.remote;

import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.Address;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
            createBackup();
            checkForOrders();
            Thread.sleep(60 * 60 * 1000);
        }
    }

    private void checkForOrders() throws Exception {
        List<Order> orders = this.runner.api.getOrderManager().getOrders(new ArrayList(), 1, Integer.MAX_VALUE);
        for (Order order : orders) {
            exportOrder(order);
        }
    }

    private void exportOrder(Order order) throws IOException, SQLException, ClassNotFoundException, Exception {
        if (addToUpdated(order)) {
            if(order.cart == null || order.cart.address == null || order.cart.address.fullName == null) {
                System.out.println("Invalid order: " + order.id);
                return;
            }
            
            System.out.println("This order need to be exported: " + order.id);
            String tostart = "> started " + new Date() + " : " + order.id;
            runner.appendToFile(tostart, "startlog.txt");
            WinVaskDBIntegration dbint = new WinVaskDBIntegration(runner);
            Integer kundenr = dbint.findCustomer(order.cart.address.fullName);
            if (kundenr == -1) {
                Address address = order.cart.address;
                try {
                    kundenr = dbint.createCustomer(address.fullName, address.address, address.postCode, address.city, address.phone, address.emailAddress);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Kundenr: " + kundenr);
            dbint.createInvoice(kundenr, order);
            tostart = "< ended " + new Date() + " : " + order.id;
            runner.appendToFile(tostart, "startlog.txt");
            dbint.close();
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

    private void createBackup() throws IOException {
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        
        File backupfolder = new File("c:\\winvask_backup");
        if(!backupfolder.exists()) {
            backupfolder.mkdirs();
        }
        
        File yearbackup = new File("c:\\winvask_backup\\"+year);
        if(!yearbackup.exists()) {
            yearbackup.mkdirs();
        }
        
        File databackupfolder = new File("c:\\winvask_backup\\"+year+"\\"+day+"-"+month);
        if(!databackupfolder.exists()) {
            databackupfolder.mkdirs();
        } else {
            return;
        }
        
        File src = new File("C:\\winvask6\\DATA\\DATA" + year);
        File dest = new File(databackupfolder.getAbsolutePath()+"\\DATA"+year);
        copyFolder(src, dest);
        
        src = new File("C:\\winvask6\\DATA\\Generelt");
        dest = new File(databackupfolder.getAbsolutePath()+"\\Generelt");
        copyFolder(src, dest);
    }

    public static void copyFolder(File src, File dest)
            throws IOException {

        if (src.isDirectory()) {

            //if directory not exists, create it
            if (!dest.exists()) {
                dest.mkdir();
                System.out.println("Directory copied from "
                        + src + "  to " + dest);
            }

            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile, destFile);
            }

        } else {
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes 
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
            System.out.println("File copied from " + src + " to " + dest);
        }
    }
}
