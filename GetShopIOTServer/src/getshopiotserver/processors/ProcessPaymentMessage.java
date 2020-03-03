/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver.processors;

import com.thundashop.core.gsd.GdsPaymentAction;
import com.thundashop.core.gsd.GetShopDeviceMessage;
import getshop.nets.GetShopNetsApp;
import getshopiotserver.GetShopIOTCommon;
import getshopiotserver.GetShopIOTOperator;
import getshopiotserver.MessageProcessorInterface;

/**
 *
 * @author boggi
 */
public class ProcessPaymentMessage extends GetShopIOTCommon implements MessageProcessorInterface {

    @Override
    public void processMessage(GetShopDeviceMessage msg) {
        if (!(msg instanceof GdsPaymentAction))
            return;
        
        logPrint("Processing payment message");
        try {
            if(msg instanceof GdsPaymentAction) {
                GdsPaymentAction paymentAction = (GdsPaymentAction) msg;
                switch(paymentAction.action) {
                    case 1:
                        logPrint("Starting payment");
                        getOperator().getPaymentOperator().startTransaction(paymentAction.amount, paymentAction.orderId);
                        break;
                    case 2:
                        logPrint("Cancelling payment");
                        getOperator().getPaymentOperator().cancelTransaction();
                        break;
                        
                    case 3:
                        logPrint("End of day report");
                        getOperator().getPaymentOperator().adminEndOfDay(paymentAction.orderId);
                        break;
                        
                    default:
                        logPrint("action: " + paymentAction.action + " not implemented yet");
                        break;
                }
            } else {
                logPrint("Failed to read payment processor message: " + msg.className);
            }
        }catch(Exception e) {
            logPrintException(e);
        }
    }
    
}
