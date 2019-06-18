/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class DevicePrintRoomCode extends GetShopDeviceMessage implements Serializable {
    public String code;
    public String roomName;
    public String printerType = "";
}
