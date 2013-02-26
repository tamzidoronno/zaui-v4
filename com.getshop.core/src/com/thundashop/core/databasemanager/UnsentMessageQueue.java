/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class UnsentMessageQueue implements Serializable {
    private ArrayList<DataObjectSavedMessage> dataObjects = new ArrayList();
    private String address;

    public UnsentMessageQueue(String address) {
        this.address = address;
    }
    
    public List<DataObjectSavedMessage> getObjects() {
        return dataObjects;
    }
    
    public void addObject(DataObjectSavedMessage data) {
        dataObjects.add(data);
        
        try (
            FileOutputStream f_out = new  FileOutputStream("data/"+address+"_queue");
            ObjectOutputStream obj_out = new ObjectOutputStream (f_out);
        ) {
            obj_out.writeObject(this);
        } catch (IOException ex) {
            Logger.getLogger(UnsentMessageQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void destroy() {
        File file = new File("data/"+address+"_queue");
        if (file.exists()) {
            file.delete();
        }
    }

    public String getAddress() {
        return address;
    }
    
    public static HashMap<String, UnsentMessageQueue> loadPersisted() {
        HashMap<String, UnsentMessageQueue> messages = new HashMap();
        File file = new File("data");
        for (File file2 : file.listFiles()) {
            if (file2.getName().contains("queue")) {
                try (
                    FileInputStream inputStream = new FileInputStream(file2);
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                ) {
                    UnsentMessageQueue unsentQueue = (UnsentMessageQueue)objectInputStream.readObject();
                    messages.put(unsentQueue.getAddress(), unsentQueue);  
                } catch (ClassNotFoundException | IOException ex) {
                    System.out.println("Warning, was not able to rebuild sending list");
                    ex.printStackTrace();
                }
            }
        }
        
        return messages;
    }
}

