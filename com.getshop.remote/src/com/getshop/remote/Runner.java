/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.remote;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;

/**
 *
 * @author boggi
 */
public class Runner {
    private String sessid;

    public static void main(String[] args) throws Exception {
        
        if(args.length == 0) {
            System.out.println("Integration menu, need argument input");
            System.out.println("1 = Start proteria integration");
            System.out.println("2 = Start winvask integration");
            System.exit(0);
        }
        
        Integer selection = -1;
        try {
            selection = new Integer(args[0]);
        }catch(Exception e) {
            
        }
        if(selection < 0 || selection > 2) {
            System.out.println("Invalid argument, choose a number between 1 - 2");
            System.exit(0);
        }
        
        Runner runner = new Runner();
        runner.start();
        switch(selection) {
            case 1:
                runner.startProteriaIntegration();
                break;
            case 2:
                runner.startWinvaskIntegration();
                break;
        }
    }
    private String address;
    private String username;
    private String password;
    public GetShopApi api;

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

    private void appendToFile(String line, String file) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        out.println(line);
        out.close();
    }

    private void start() throws Exception {
        sessid = UUID.randomUUID().toString();
        connectToBackend();
    }
    
    private void startProteriaIntegration() throws Exception {
        System.out.println("Starting proteria integration");
        ComGetshopProteriasync runner = new ComGetshopProteriasync(this);
        runner.startClient();
    }

    private void startWinvaskIntegration() throws Exception {
        System.out.println("Starting winvask integration");
        ComGetShopWinvask winvask = new ComGetShopWinvask(this);
        winvask.startClient();
    }
}
