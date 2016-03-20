package com.thundashop.core.ftpmanager;

import com.getshop.scope.GetShopSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class FtpManager implements IFtpManager {
    public boolean transferFile(String username, String password, String hostname, String filePath, String location, Integer port) throws IOException {
        /**
         * CAREFUL HERE... By exposing this public, you give access to upload whatever file to whoever.....
         * THIS IS A MAJOR SECURITY RISK! FILEPATH IS NOT SECURE!!!!!!!
         */
        FTPClient client = new FTPClient();
        client.connect(hostname, port);
        client.login(username, password);
        client.enterLocalPassiveMode();
        client.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            System.out.println("Failed to connect to ftp server: " + hostname + " with username: " + username);
            return false;
        }

        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        location = location.replaceAll(":", "_");
        client.changeWorkingDirectory(location);
        boolean done = client.storeFile(file.getName(), inputStream);
        inputStream.close();
        client.disconnect();
        if (!done) {
            System.out.println("Failed to transfer file : " + file.getName() + " to location: " + location);
            return false;
        } else {
            return true;
        }
    }

}
