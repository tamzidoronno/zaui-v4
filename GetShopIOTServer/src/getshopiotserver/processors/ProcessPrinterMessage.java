/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver.processors;

import com.thundashop.core.gsd.DevicePrintMessage;
import com.thundashop.core.gsd.GetShopDeviceMessage;
import getshopiotserver.GetShopIOTCommon;
import getshopiotserver.MessageProcessorInterface;

/**
 *
 * @author boggi
 */
public class ProcessPrinterMessage extends GetShopIOTCommon implements MessageProcessorInterface {
    
    @Override
    public void processMessage(GetShopDeviceMessage msg) {
        DevicePrintMessage printmsg = (DevicePrintMessage) msg;
        logPrint("Printing reciept" + printmsg.paymentMethod);
    }
    
}
