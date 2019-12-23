/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

/**
 *
 * @author boggi
 */
public class GdsPaymentAction extends GetShopDeviceMessage {
    
    public static class Actions {
        public static Integer STARTPAYMENT = 1;
        public static Integer CANCELPAYMENT = 2;
    }
    
    public Integer action = 0;
    public Integer amount = 0;
    public String orderId = "";
}
