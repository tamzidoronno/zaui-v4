package com.thundashop.core.pullserver;


import com.getshop.scope.GetShopSession;
import java.util.Calendar;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.CheckOrderCollector;
import com.thundashop.core.pullserver.data.PullMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PullServerManager extends ManagerBase implements IPullServerManager {
    private ArrayList<PullMessage> pullMessages = new ArrayList();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataRetrieved : data.data) {
            if (dataRetrieved instanceof PullMessage) {
                PullMessage pullMessage = (PullMessage)dataRetrieved;
                if (!pullMessage.delivered) {
                    pullMessages.add(pullMessage);
                }
            }
        }
        
        if (storeId.equals("2a831774-e72e-43e3-ac4c-d8700a402e52")) {
            createScheduler("trigger", "*/5 * * * *", CheckForPullMessages.class);
        }
    }
    
    @Override
    public void savePullMessage(PullMessage pullMessage) {
        pullMessage.sequence = getCounter();
        saveObject(pullMessage);
        pullMessages.add(pullMessage);
        incrementSequence();
        triggerCheckForPullMessage();
    }

    private int getCounter() {
        String counterString = getManagerSetting("counter");
        if (counterString == null) {
            counterString = "1";
        }
        
        return Integer.parseInt(counterString);
    }

    private void incrementSequence() {
        int counter = getCounter();
        counter++;
        setManagerSetting("counter", ""+counter);
    }

    @Override
    public List<PullMessage> getPullMessages(String keyId, String storeId) {
        return pullMessages.stream()
                .filter(o -> o.belongsToStore.equals(storeId))
                .filter(o -> !o.isInvalidatedDueToTime())
                .filter(o -> o.keyId.equals(keyId) || keyId.equals("getshop_all_message_for_store_to_receive"))
                .collect(Collectors.toList());
    }

    @Override
    public void markMessageAsReceived(String messageId, String storeId) {
        PullMessage pullMessage = pullMessages.stream()
                .filter(o -> o.id.equals(messageId))
                .filter(o -> o.belongsToStore.equals(storeId))
                .findFirst().orElse(null);
        
        if (pullMessage != null) {
            pullMessage.delivered = true;
            pullMessages.remove(pullMessage);
            saveObject(pullMessage);
        }
    }

    @Override
    public void triggerCheckForPullMessage() {
//        PullMessage testMsg = new PullMessage();
//        testMsg.belongsToStore = "13442b34-31e5-424c-bb23-a396b7aeb8ca";
//        testMsg.body = "TEST";
//        testMsg.rowCreatedDate = new Date();
//        testMsg.id = UUID.randomUUID().toString();
//        
//        pullMessages.add(testMsg);
        
        List<String> storeIds = pullMessages.stream()
                .filter(o -> !o.isInvalidatedDueToTime())
                .map(o -> o.belongsToStore)
                .collect(Collectors.toList());

        storeIds = new ArrayList<String>(new HashSet<String>(storeIds));

        for (String iStoreId : storeIds) {

            Runnable runThread = new Runnable() {
                @Override
                public void run() {
                    String address = "https://www.getshop.com/";

                    if (!frameworkConfig.productionMode) {
                        address = "http://getshopnew.3.0.local.getshop.com/";
                    }

                    address += "scripts/triggercheckordertocollect.php?storeid=";
                    address += iStoreId;
                    try { 
                        openAddress(address);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

            Thread td = new Thread(runThread);
            td.setName("Check for pull message");
            td.start();
        }
    }

    
    
    public String openAddress(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    
}
