/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class AddressList implements Serializable {
    private File addressListFile = new File("data/addresslist");
    private List<String> addresses = new ArrayList();
//    private final Logger log;
    
    AddressList(Logger log) {
//        this.log = log;
        loadList();
    }
    
    private void loadList() {
        if (!addressListFile.exists()) {
            printHookupMessages();
            return;
        }
        
        try (
            FileInputStream inStream = new FileInputStream(addressListFile);
            ObjectInputStream objectStream = new ObjectInputStream(inStream);
        ) {
            addresses = (List<String>)objectStream.readObject();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex2) {
            ex2.printStackTrace();
        } catch (IOException ex3) {
            ex3.printStackTrace();
        }
        
        if (addresses.size() < 2) {
            printHookupMessages();
            return;
        }
    }
    
    private void printHookupMessages() {
        System.out.println("This server is not connected to a getshop network. Running standalone");
    }
    
     private boolean isMyIpaddress(String ip) {
        Enumeration<NetworkInterface> interfaces;
        
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());            
        }
        
        while(interfaces.hasMoreElements()) {
            NetworkInterface netInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address.getHostAddress().equals(ip)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public List<String> getIpaddressesThatAreNotMine() {
        List<String> retaddresses = new ArrayList();
        
        for (String address : addresses) {
            if (!isMyIpaddress(address)) {
                retaddresses.add(address);
            } 
        }
        
        return retaddresses;
    }

    public String getMyAddress() {
        for (String address : addresses) {
            if (isMyIpaddress(address)) {
                return address;
            }
        }
        
        return "";
    }

    public void register(String address) {
        if (!addresses.contains(address)) {
            addresses.add(address);
        }
        
        saveToDisk();
    }
    
    private void saveToDisk() {
        try (
            FileOutputStream outFileStream = new FileOutputStream(addressListFile);
            ObjectOutputStream outStream = new ObjectOutputStream(outFileStream);
        ) {
            outStream.writeObject(addresses);
        } catch (FileNotFoundException ex) {
            
        } catch (IOException ex2) {
            
        }
        
    }

    void clear() {
        addresses = new ArrayList();
        saveToDisk();
    }
}
