package com.getshop.syncserver;

import com.thundashop.api.managers.GetShopApi;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import com.thundashop.core.appmanager.data.AvailableApplications;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class MonitorOutgoingEvents extends Thread {

    private final Socket socket;
    private final GetShopApi api;
    private String appPath = "../com.getshop.client/app/";
    private PrintWriter out;
    private final DataOutputStream output;
    private boolean disconnected = false;

    public MonitorOutgoingEvents(Socket socket, GetShopApi api) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.api = api;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<ApplicationSynchronization> allApps = api.getAppManager().getSyncApplications();
                for (ApplicationSynchronization sync : allApps) {
                    ApplicationSettings settings = api.getAppManager().getApplication(sync.appId);
                    System.out.println("Need to be synchronized: " + settings.appName);
                    uploadApplication(settings);
                    api.getAppManager().saveApplication(settings);
                    String namespace = convertToNameSpace(settings.id);
                    System.out.println("Namespace: " + namespace);
                    writeLineToSocket("STARTSYNC");
                    pushAllFiles(new File(appPath + "/" + namespace), settings, null);
                    writeLineToSocket("ENDSYNC");
                }
                if(disconnected) {
                    break;
                }

                sleep(2000);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Connection broken");
                break;
            }
        }
        System.out.println("Cleaning up outgoing events");
    }

    private String convertToNameSpace(String uuid) {
        uuid = "ns_" + uuid.replace("-", "_");
        return uuid;
    }

    private void uploadApplication(ApplicationSettings settings) {
        System.out.println("Uploading application: " + settings.appName + " id: " + settings.id);

    }

    private void pushAllFiles(File allFiles, ApplicationSettings settings, ArrayList<String> excludeList) throws IOException {
        if (!allFiles.exists()) {
            System.out.println("Could not find app path: " + allFiles.getAbsolutePath());
            return;
        }
        File[] fileList = allFiles.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                pushAllFiles(file, settings, excludeList);
            } else {
                String uploadPath = file.getAbsolutePath().replace(new File(appPath).getAbsolutePath(), "");
                String namespace = convertToNameSpace(settings.id);
                uploadPath = uploadPath.replace(namespace, settings.appName);
                boolean ignore = false;
                if(excludeList != null) {
                    for(String localPath : excludeList) {
                        if(localPath.endsWith(uploadPath)) {
                            ignore = true;
                            break;
                        }
                    }
                }
                
                if(!ignore) {
                    pushFile(uploadPath, file);
                }
            }
        }
    }
    
    private void pushFile(String uploadPath, File file) throws IOException {
        System.out.println("Pushing file : " + file.getAbsolutePath());

        writeLineToSocket("FILESYNC");
        writeLineToSocket(uploadPath);
        writeLineToSocket(file.length()+ "");

        byte[] array = new byte[1024];
        InputStream in = new FileInputStream(file);
        long total = 0;
        while (true) {
            int read = in.read(array);
            if(read > 0) {
                output.write(array, 0, read);
                output.flush();
                total += read;
            } else {
                break;
            }
        }
        System.out.println("Pushed: " + total +  " size");
        in.close();
    }

    private void writeLineToSocket(String line) throws IOException {
        out.println(line);
        out.flush();
    }

    void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    void doPush(ArrayList<String> excludeList) throws Exception {
        AvailableApplications allapps = api.getAppManager().getAllApplications();
        String storeid = api.getStoreManager().getStoreId();
        writeLineToSocket("STARTSYNC");
        for(ApplicationSettings settings : allapps.applications) {
            if(settings.ownerStoreId.equals(storeid)) {
                String namespace = convertToNameSpace(settings.id);
                pushAllFiles(new File(appPath + "/" + namespace), settings, excludeList);
            }
        }
        writeLineToSocket("ENDSYNC");
    }
    
}
