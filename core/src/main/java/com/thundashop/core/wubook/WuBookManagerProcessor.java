
package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSchedulerBase;
import java.util.List;

public class WuBookManagerProcessor extends GetShopSchedulerBase {

    public WuBookManagerProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }    
    
    @Override
    public void execute() throws Exception {
        System.out.println("Searching for new bookings");
//        getApi().getWubookManager().addNewBookingsPastDays(getMultiLevelName(), 2);
        
    }
    
}
