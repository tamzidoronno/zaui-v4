/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.PermenantlyDeleteData;

/**
 *
 * @author ktonder
 */
@PermenantlyDeleteData
public class VirtualOrder extends DataCommon {
    public Order order;
    public String reference;
}
