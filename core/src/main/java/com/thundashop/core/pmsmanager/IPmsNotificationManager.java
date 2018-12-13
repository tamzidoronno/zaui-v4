/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

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
    public List<PmsNotificationMessage> getAllMessages();
    public void saveMessage(PmsNotificationMessage msg);
    public PmsNotificationMessage getMessage(String messageId);
}
