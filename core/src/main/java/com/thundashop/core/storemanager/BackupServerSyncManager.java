package com.thundashop.core.storemanager;

import com.getshop.scope.GetShopSessionScope;
import com.google.gson.Gson;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.getshoplocksystem.GetShopLockSystemManager;
import com.thundashop.core.getshoplocksystem.LockServer;
import com.thundashop.core.getshoplocksystem.LockServerBase;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.gsd.GetShopDevice;
import com.thundashop.core.webmanager.WebManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BackupServerSyncManager {

    @Autowired
    private StoreManager storeManager;
    @Autowired
    private GetShopLockSystemManager getShopLockSystemManager;
    @Autowired
    private GdsManager gdsManager;
    @Autowired
    private WebManager webManager;
    @Autowired
    GetShopSessionScope getShopSessionScope;

    private static final String backupSystemURL = "https://api.pibackup.zauistay.tech/api/sync";
    private static final String backupSystemApiKey = "z@uiBack$tay2022";
    @Async("backupServerSyncExecutor")
    public void doubleCheckTransferServersToBackupSystem(String storeId) {

        getShopSessionScope.setStoreId(storeId, "", null);

        try {
            List<LockServer> lockServers = getShopLockSystemManager.getLockServersUnfinalized();
            List<BackupServerInfo> toSendList = new ArrayList<>(lockServers.size());

            for (LockServer server : lockServers) {
                RemoteServerMetaData serverInfo = storeManager.getBackupServerInfoByServerId(server.getId());

                if (serverInfo.beenTransferred) {
                    continue;
                }

                BackupServerInfo info = new BackupServerInfo();
                LockServerBase base = (LockServerBase) server;
                info.setData(base);
                toSendList.add(info);
            }

            List<GetShopDevice> devices = gdsManager.getDevices();
            for (GetShopDevice dev : devices) {
                RemoteServerMetaData serverInfo = storeManager.getBackupServerInfoByServerId(dev.id);

                if (serverInfo.beenTransferred) {
                    continue;
                }

                BackupServerInfo info = new BackupServerInfo();
                info.setData(dev);
                toSendList.add(info);
            }

            for (BackupServerInfo info : toSendList) {
                try {
                    info.webaddr = storeManager.getMyStore().getDefaultWebAddress();
                    info.storeId = storeId;
                    String toSend = new Gson().toJson(info);
                    String result = webManager.htmlPostBasicAuth(backupSystemURL ,toSend,true,"UTF-8", backupSystemApiKey);
                    if(StringUtils.isBlank(result)) {
                        storeManager.logPrintException(new ErrorException(500, "Result not found!"));
                        continue;
                    }

                    if (result.equals("added")) {
                        RemoteServerMetaData serverInfo = new RemoteServerMetaData();
                        serverInfo.serverId = info.id;
                        serverInfo.beenTransferred = true;
                        storeManager.saveObject(serverInfo);
                    }

                } catch (Exception e) {
                    storeManager.logPrintException(e);
                }
            }
        } finally {
            getShopSessionScope.removethreadStoreId(storeId);
        }

    }

}
