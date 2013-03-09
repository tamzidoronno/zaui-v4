package com.getshop.syncserver;

import com.thundashop.api.managers.GetShopApi;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import java.net.Socket;
import java.util.List;

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
                List<ApplicationSynchronization> allApps = api.getAppManager().getSyncApplications();
                for(ApplicationSynchronization sync : allApps) {
                    ApplicationSettings settings = api.getAppManager().getApplication(sync.appId);
                    System.out.println("Need to be synchronized: " + settings.appName);
                    uploadApplication(settings);
                    api.getAppManager().saveApplication(settings);
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
        System.out.println("Uploading application: " + settings.appName +  " id: " + settings.id);
        
    }
   
}
