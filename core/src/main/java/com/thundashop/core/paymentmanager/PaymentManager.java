/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.paymentmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshopaccounting.GetShopAccountingManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PaymentManager extends ManagerBase implements IPaymentManager {
    private HashMap<String, StorePaymentConfig> storePaymentConfig = new HashMap();
    private HashMap<String, PaymentConfiguration> paymentConfigs = new HashMap();
    private GeneralPaymentConfig generalConfig = new GeneralPaymentConfig();
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private GetShopAccountingManager getShopAccountingManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        loadDataFromRemoteDatabase();
        
        for (DataCommon commonData : data.data) {
            if (commonData instanceof PaymentConfiguration) {
                paymentConfigs.put(commonData.id, (PaymentConfiguration)commonData);
            }
            if (commonData instanceof GeneralPaymentConfig) {
                if(commonData.id == null || commonData.id.isEmpty()) {
                    continue;
                }
                generalConfig = (GeneralPaymentConfig)commonData;
            }
            if (commonData instanceof StorePaymentConfig) {
                storePaymentConfig.put(commonData.id, (StorePaymentConfig)commonData);
            }
        }
    }

    @Override
    public void savePaymentConfiguration(PaymentConfiguration config) {
        if (GetShopLogHandler.isDeveloper) {
            config.storeId = "all_cms";
            databaseRemote.save(config, createRemoteCredentials());
            paymentConfigs.put(config.id, config);
        } 
    }

    private void loadDataFromRemoteDatabase() {
        Stream<DataCommon> data = databaseRemote.getAll("PaymentManager", "all", "cms");
        data.forEach(d -> {
            if (d instanceof PaymentConfiguration) {
                paymentConfigs.put(d.id, (PaymentConfiguration)d);
            }
        });
    }

    private Credentials createRemoteCredentials() {
        Credentials cred = new Credentials();
        cred.manangerName = "PaymentManager";
        cred.storeid = "all";
        return cred;
    }

    @Override
    public PaymentConfiguration getConfig(String id) {
        return paymentConfigs.get(id);
    }

    public boolean handleOrder(Order order) {
        Date lockDate = getShopAccountingManager.getPreviouseEndDate();
       
        if (order.transferredToAccountingSystem) {
            return false;
        }
        
        if (order.isNullOrder()) {
            return false;
        }
        
        double totalForOrder = orderManager.getTotalAmount(order);
        
        PaymentConfiguration config = getConfigForOrder(order);
        
        if (config == null) {
            return false;
        }
        
        if (totalForOrder < 0) {
            return handleCreditNote(config, order, lockDate);
        } else {
            return handleNormalOrder(config, order, lockDate);
        }
    }

    private boolean handleNormalOrder(PaymentConfiguration config, Order order, Date lockDate) {
        if (config.transferToAccountingBasedOnPaymentDate && order.paymentDate != null && order.status == Order.Status.PAYMENT_COMPLETED) {
            order.shouldHaveBeenTransferredToAccountingOnDate = order.paymentDate;
            order.transferToAccountingDate = order.paymentDate;
            
            if (order.paymentDate.before(lockDate) || order.paymentDate.equals(lockDate)) {
                order.transferToAccountingDate = getOneMinuteLater(lockDate);
            }
            
            return true;
        }
        
        if (config.transferToAccountingBasedOnCreatedDate) {
            order.shouldHaveBeenTransferredToAccountingOnDate = order.createdDate;
            order.transferToAccountingDate = order.createdDate;
            
            if (order.createdDate.before(lockDate) || order.createdDate.equals(lockDate)) {
                order.transferToAccountingDate = getOneMinuteLater(lockDate);
            }
            
            return true;
        }
        
        return false;
    }

    private boolean handleCreditNote(PaymentConfiguration config, Order order, Date lockDate) {
        if (config.transferCreditNoteToAccountingBasedOnPaymentDate && order.paymentDate != null) {
            order.shouldHaveBeenTransferredToAccountingOnDate = order.paymentDate;
            order.transferToAccountingDate = order.paymentDate;
            
            if (order.paymentDate.before(lockDate) || order.paymentDate.equals(lockDate)) {
                order.transferToAccountingDate = getOneMinuteLater(lockDate);
            }
            
            return true;
        }
        
        if (config.transferCreditNoteToAccountingBasedOnCreatedDate) {
            order.shouldHaveBeenTransferredToAccountingOnDate = order.createdDate;
            order.transferToAccountingDate = order.createdDate;
            
            if (order.createdDate.before(lockDate) || order.createdDate.equals(lockDate)) {
                order.transferToAccountingDate = getOneMinuteLater(lockDate);
            }
            
            return true;
        }
        
        return false;
    }

    private PaymentConfiguration getConfigForOrder(Order order) {
        if (order.payment == null)
            return null;
        
        String paymentId = order.getPaymentApplicationId();
        return getConfig(paymentId);
    }

    private Date getOneMinuteLater(Date lockDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(lockDate);
        cal.add(Calendar.MINUTE, 1);
        return cal.getTime();
    }

    @Override
    public void saveStorePaymentConfiguration(StorePaymentConfig config) {
        StorePaymentConfig oldConfig = getStorePaymentConfiguration(config.paymentAppId);
        
        if (oldConfig != null) {
            config.id = oldConfig.id;
        }
        
        saveObject(config);
        storePaymentConfig.put(config.id, config);
    }

    @Override
    public StorePaymentConfig getStorePaymentConfiguration(String paymentAppId) {
        return storePaymentConfig.values()
                .stream()
                .filter(o -> o.paymentAppId.equals(paymentAppId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<StorePaymentConfig> getStorePaymentConfigurations(String paymentAppId) {
        return new ArrayList(storePaymentConfig.values());
    }

    @Override
    public GeneralPaymentConfig getGeneralPaymentConfig() {
        return generalConfig;
    }

    @Override
    public void saveGeneralPaymentConfig(GeneralPaymentConfig config) {
        if (generalConfig != null && generalConfig.id != null && !generalConfig.id.isEmpty()) {
            config.id = generalConfig.id;
        }
        
        saveObject(config);
        generalConfig = config;
    }

    @Override
    public void resetAllAccountingConfigurationForUsersAndOrders(String password) {
        if(!password.equals("fngfi0456tbvxdFREtgdfs")) {
            return;
        }
        
        List<User> users = userManager.getAllUsers();
        for(User usr : users) {
            boolean save = false;
            if(usr.externalAccountingId != null && !usr.externalAccountingId.isEmpty()) {
                usr.externalAccountingId = null;
                save = true;
            }
            if(usr.accountingId != null && !usr.accountingId.isEmpty()) {
                usr.accountingId = "";
                save = true;
            }
            if(save) {
                userManager.saveUser(usr);
            }
        }
    }

    public boolean isAllowedToMarkAsPaid(String paymentId) {
        if (paymentId == null) {
            return false;
        }
        
        if (paymentId.equals("6dfcf735-238f-44e1-9086-b2d9bb4fdff2")) {
            return false;
        }
        
        return true;
    }
    
}
