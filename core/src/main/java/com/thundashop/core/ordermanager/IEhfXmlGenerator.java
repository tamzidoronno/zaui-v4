/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IEhfXmlGenerator {
    
    @Administrator
    public String generateXml(String orderId);
}
