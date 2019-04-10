/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver;

import com.thundashop.core.gsd.GetShopDeviceMessage;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class GetShopIOTCommon extends Thread {
    GetShopIOTOperator operator = null;
    
    public void setIOTOperator(GetShopIOTOperator operator) {
        this.operator = operator;
    }
    
    public GetShopIOTOperator getOperator() {
        return this.operator;
    }
    
    public void logPrint(String msg) {
        System.out.println(new Date() + " : " + msg);
    }
    public void logPrintWithoutLine(String msg) {
        System.out.print(msg);
    }
    public void logPrintException(Exception e) {
        e.printStackTrace();
    }
    
    public void sendMessage(GetShopDeviceMessage msg) {
        
    }
}
