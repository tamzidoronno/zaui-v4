/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.director;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.SmsMessage;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.system.SystemManager;
import com.thundashop.core.usermanager.UserManager;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class DirectorManager extends ManagerBase implements IDirectorManager {
    
    public static String password = "adsfu9o21n4jl12n341kj2bn3asdfas0f9jqowierjqkljnr54lkbdslbflabfdkajbfdkafbdaskdfb";
    
    @Autowired
    private GetShopSessionScope scope; 
    
    @Autowired
    private SystemManager systemManager;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private StorePool storePool;
    
    @Autowired
    private MessageManager messageManager;
    
    @Autowired
    private OrderManager orderManager;
    
    private DirectorySyncUtils syncUtils;
    
    @Override
    public void syncFromOld() {
        syncUtils.createSystems();
    }
    
    @PostConstruct
    public void initted() {
        this.syncUtils = new DirectorySyncUtils(scope, systemManager, userManager, storePool);
    }
    
    @Override
    public DailyUsage getDailyUsage(String password, Date date) {
        checkPassword(password);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        
        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();
        
        List<SmsMessage> smses = messageManager.getAllSmsMessages(start, end);
        
        String defaultPrefix = getStoreDefaultPrefix();
        DailyUsage usage = new DailyUsage();
        
        usage.domesticSmses = smses.stream()
                .filter(o -> o.prefix.trim().equals(defaultPrefix))
                .mapToInt(o -> (int)Math.ceil((double)o.message.length() / (double)164))
                .sum();
        
        usage.internationalSmses = smses.stream()
                .filter(o -> !o.prefix.trim().equals(defaultPrefix))
                .mapToInt(o -> (int)Math.ceil((double)o.message.length() / (double)164))
                .sum();
        
        usage.ehfs = orderManager.getEhfSentLog(start, end).size();
        usage.start = start;
        usage.end = end;
        usage.belongsToStoreId = getStoreId();
        
        return usage;
    }

    private void checkPassword(String password1) throws ErrorException {
        if (!password1.equals(this.password)) {
            throw new ErrorException(26);
        }
    }

    @Override
    public Date getCreatedDate(String password) {
        checkPassword(password);
        return getStore().rowCreatedDate;
    }
    
}
