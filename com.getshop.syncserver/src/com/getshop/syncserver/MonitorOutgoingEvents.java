package com.getshop.syncserver;

import com.thundashop.api.managers.GetShopApi;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author boggi
 */
public class MonitorOutgoingEvents extends Thread {

    private final Socket socket;
    private final GetShopApi api;
    private String appPath = "/home/boggi/projects/core/com.getshop.client/app/";
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
                    pushAllFiles(new File(appPath + "/" + namespace), settings);
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
        uuid = uuid.replace("0", "i");
        uuid = uuid.replace("1", "j");
        uuid = uuid.replace("2", "k");
        uuid = uuid.replace("3", "l");
        uuid = uuid.replace("4", "m");
        uuid = uuid.replace("5", "n");
        uuid = uuid.replace("6", "o");
        uuid = uuid.replace("7", "p");
        uuid = uuid.replace("8", "q");
        uuid = uuid.replace("9", "r");
        uuid = uuid.replace("-", "");
        return uuid;
    }

    private void uploadApplication(ApplicationSettings settings) {
        System.out.println("Uploading application: " + settings.appName + " id: " + settings.id);

    }

    private void pushAllFiles(File allFiles, ApplicationSettings settings) throws IOException {
        if (!allFiles.exists()) {
            System.out.println("Could not find app path: " + allFiles.getAbsolutePath());
            return;
        }
        File[] fileList = allFiles.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                pushAllFiles(file, settings);
            } else {
                String uploadPath = file.getAbsolutePath().replace(appPath, "");
                String namespace = convertToNameSpace(settings.id);
                uploadPath = uploadPath.replace(namespace, settings.appName);
                pushFile(uploadPath, file);
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
}
