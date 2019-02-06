/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.List;

/**
 *
 * @author boggi
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPmsNotificationManager {
    @Administrator
    public List<PmsNotificationMessage> getAllMessages();
    @Administrator
    public void saveMessage(PmsNotificationMessage msg);
    @Administrator
    public PmsNotificationMessage getMessage(String messageId);
    @Administrator
    public void deleteMessage(String messageId);
    @Administrator
    public List<String> getLanguagesForMessage(String key, String type);
    @Administrator
    public List<String> getPrefixesForMessage(String key, String type);
    
    @Administrator
    public PmsNotificationMessage getSpecificMessage(String key, PmsBooking booking, PmsBookingRooms room, String type, String prefix);
}
