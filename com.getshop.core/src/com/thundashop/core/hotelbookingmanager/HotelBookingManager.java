package com.thundashop.core.hotelbookingmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class HotelBookingManager extends ManagerBase implements IHotelBookingManager {

    @Override
    public void dataFromDatabase(DataRetreived data) {
     
    }   
}