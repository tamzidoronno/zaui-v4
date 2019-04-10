/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver;

import com.thundashop.core.gsd.GetShopDeviceMessage;

/**
 *
 * @author boggi
 */
public interface MessageProcessorInterface {
    
    public void processMessage(GetShopDeviceMessage msg);
    public void setIOTOperator(GetShopIOTOperator operator);
}
