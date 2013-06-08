/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.utils;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import java.net.UnknownHostException;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class LocateById {
    
    private  Credentials createCredentials(Class clazz, String storeid) {
        Credentials cred = new Credentials(clazz);
        cred.storeid = storeid;
        cred.manangerName = clazz.getSimpleName();
        cred.password = "#¤";
        return cred;
    }
    
    public void start(String id) throws UnknownHostException {
        Database database = new Database();
        List<DataCommon> stores = database.retreiveData(createCredentials(StoreManager.class, "all"));
        for (DataCommon storeCommon : stores) {
            Store store = (Store)storeCommon;
            DataCommon data = findData(database, store, id);
            if (store.id.equals(id)) {
                printMessage(store, null);
                return;
            }
        }
    }
    
    private void printMessage(Store store, DataCommon object) {
        System.out.println("Store: " + store.id + " address: " + store.webAddress);
        System.out.println("Data: " + object.id + " object: " + object.className);
        
        System.exit(1);
    }
    
    public static void main(String args[]) throws UnknownHostException {
          
    }

    private DataCommon findData(Database database, Store store, String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
