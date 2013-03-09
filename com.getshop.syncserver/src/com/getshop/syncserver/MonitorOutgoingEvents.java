/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.syncserver;

import com.thundashop.api.managers.GetShopApi;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author boggi
 */
public class MonitorOutgoingEvents extends Thread {
    private final Socket socket;
    private final GetShopApi api;

    public MonitorOutgoingEvents(Socket socket, GetShopApi api) {
        this.socket = socket;
        this.api = api;
    }
    
    @Override
    public void run() {
        while(true) {
            try {
                List<ApplicationSettings> allApps = api.getAppManager().getAllApplications();
                for(ApplicationSettings settings : allApps) {
                    if(settings.synchronize) {
                        System.out.println("Need to be synchronized: " + settings.appName);
                        uploadApplication(settings);
                        settings.synchronize = false;
                        api.getAppManager().saveApplication(settings);
                    }
                }
            
                sleep(2000);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Connection broken");
                break;
            }
        }
    }

    private void uploadApplication(ApplicationSettings settings) {
        System.out.println("Uploading application: " + settings.appName);
    }
   
}
