/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver.processors;

import com.thundashop.core.gsd.DevicePrintMessage;
import com.thundashop.core.gsd.GdsAccessDenied;
import com.thundashop.core.gsd.GetShopDeviceMessage;
import getshopiotserver.GetShopIOTCommon;
import getshopiotserver.MessageProcessorInterface;

/**
 *
 * @author boggi
 */
public class ProcessAccessDenied extends GetShopIOTCommon implements MessageProcessorInterface {
    
    @Override
    public void processMessage(GetShopDeviceMessage msg) {
        if (!(msg instanceof GdsAccessDenied))
            return;
        
        logPrint("Access denied");
        try { Thread.sleep(120000); }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
