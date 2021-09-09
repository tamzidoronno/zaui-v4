package com.thundashop.core.storemanager;

import com.getshop.scope.GetShopSessionScope;
import com.google.gson.Gson;
import com.thundashop.core.getshoplocksystem.GetShopLockSystemManager;
import com.thundashop.core.getshoplocksystem.LockServer;
import com.thundashop.core.getshoplocksystem.LockServerBase;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.gsd.GetShopDevice;
import com.thundashop.core.webmanager.WebManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
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
                    String toSend = URLEncoder.encode(new Gson().toJson(info), "UTF-8");
                    String result = webManager.htmlGet("http://10.0.7.10:8800/?setServer=" + toSend);

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
