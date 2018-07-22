/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.stripe;

import com.thundashop.core.common.GetShopApi;

/**
 *
 * @author boggi
 */
@GetShopApi
public interface IStripeManager {
    public boolean createAndChargeCustomer(String orderId, String token);
    public boolean chargeOrder(String orderId, String cardId);
}
