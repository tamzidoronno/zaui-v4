package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.ArrayList;

@GetShopApi
public interface IHotelBookingManager {

    /**
     * 
     * @param name
     * @param description 
     */
    @Administrator
    public void createDomain(String name, String description);
    
    /**
     * 
     * @return 
     */
    public ArrayList<Domain> getDomains();
    
    @Administrator
    public RoomType createRoomType(String domainId, String name, double price, int size);   
}
