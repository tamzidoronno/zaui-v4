/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ftpmanager;

import com.thundashop.core.common.GetShopLogHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author boggi
 */
public class FTPTransferrer extends Thread {
    private String hostname;
    private String password;
    private String username;
    private String filePath;
    private String location;
    private Integer port;
    private boolean useActiveMode = false;
    private Integer minutesToSleep = 0;
    private String storeId;

    public FTPTransferrer(String hostname, String password, String username, String filePath, String location, Integer port, Integer minutesToSleep) {
        this.hostname = hostname;
        this.password = password;
        this.username = username;
        this.filePath = filePath;
        this.location = location;
        this.port = port;
        this.minutesToSleep = minutesToSleep;
    }
    
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
    
    
    @Override
    public void run() {
        try {
            Thread.sleep(minutesToSleep * (60*1000));
            /**
            * CAREFUL HERE... By exposing this public, you give access to upload whatever file to whoever, DO NOT MAKE THIS AS A PUBLIC API CALL.....
            * THIS IS A MAJOR SECURITY RISK! FILEPATH IS NOT SECURE!!!!!!!
            */
           FTPClient client = new FTPClient();
           client.connect(hostname, port);
           client.login(username, password);
           if(!useActiveMode) {
               client.enterLocalPassiveMode();
           }
           client.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
           int reply = client.getReplyCode();
           if (!FTPReply.isPositiveCompletion(reply)) {
               GetShopLogHandler.logPrintStatic("Failed to connect to ftp server: " + hostname + " with username: " + username, storeId);
           }

           File file = new File(filePath);
           InputStream inputStream = new FileInputStream(file);
           location = location.replaceAll(":", "_");
           client.changeWorkingDirectory(location);
           boolean done = client.storeFile(file.getName(), inputStream);
           inputStream.close();
           client.disconnect();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
