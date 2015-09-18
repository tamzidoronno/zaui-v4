package com.thundashop.core.pullserver;


import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pullserver.data.PullMessage;
import java.util.ArrayList;
import java.util.List;
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
    }
    
    @Override
    public void savePullMessage(PullMessage pullMessage) {    
        pullMessage.sequence = getCounter();
        saveObject(pullMessage);
        pullMessages.add(pullMessage);
        incrementSequence();
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
                .filter(o -> o.keyId.equals(keyId))
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
    
    
}
