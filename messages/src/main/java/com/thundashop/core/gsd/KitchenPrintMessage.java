/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

import com.thundashop.core.cartmanager.data.CartItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class KitchenPrintMessage extends GetShopDeviceMessage implements Serializable {
    public List<CartItem> cartItems = new ArrayList();
    public String printedBy = "";
    public String tabName = "";
    public String header = "";
    public Date date = new Date();
}
