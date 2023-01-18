package com.thundashop.core.ftpmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.services.config.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class FtpManager extends ManagerBase implements IFtpManager {
    @Autowired
    FrameworkConfig frameworkConfig;
    
    public boolean transferFile(String username, String password, String hostname, String filePath, String location, Integer port, boolean useActiveMode, int minutesToWait) throws IOException {
        if(!frameworkConfig.productionMode) {
            logPrint("Transferring file: " + filePath + " to " + location + ":" + port + " user: " + username);
            return true;
        }
        
        /**
         * CAREFUL HERE... By exposing this public, you give access to upload whatever file to whoever, DO NOT MAKE THIS AS A PUBLIC API CALL.....
         * THIS IS A MAJOR SECURITY RISK! FILEPATH IS NOT SECURE!!!!!!!
         */
        FTPTransferrer trans = new FTPTransferrer(hostname, password, username, filePath, location, port, minutesToWait);
        trans.setName("FTP Transfer thread");
        trans.setStoreId(storeId);
        trans.start();
        return true;
    }

}
