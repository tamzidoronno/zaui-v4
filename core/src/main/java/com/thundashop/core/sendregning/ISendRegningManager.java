/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sendregning;

import com.thundashop.core.common.GetShopApi;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author boggi
 */
@GetShopApi
public interface ISendRegningManager {
    @Autowired
    public String sendOrder(String orderId);
}
