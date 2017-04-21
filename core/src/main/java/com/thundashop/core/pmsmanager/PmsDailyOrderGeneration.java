package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsDailyOrderGeneration extends GetShopSessionBeanNamed {
    
    @Autowired
    PmsManager pmsManager;
    
    public String createOrder(String bookingId, NewOrderFilter filter) {
        
        return "";
    }
    
}
