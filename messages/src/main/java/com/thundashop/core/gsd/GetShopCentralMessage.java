/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.gsd;

/**
 *
 * @author ktonder
 */
public class GetShopCentralMessage extends GetShopDeviceMessage {
    public String message = "";

    public GetShopCentralMessage() {
    }

    public GetShopCentralMessage(String msg) {
        this.message = msg;
    }
}
