
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSchedulerBase;

public class CareTakerDailyProcessor extends GetShopSchedulerBase {

    public CareTakerDailyProcessor(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    @Override
    public void execute() throws Exception {
        String name = getMultiLevelName();
        getApi().getCareTakerManager().checkForTasksToCreate(getMultiLevelName());
    }
    
    
}
