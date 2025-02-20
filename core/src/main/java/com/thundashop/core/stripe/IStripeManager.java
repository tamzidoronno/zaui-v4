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
    public String createSessionForPayment(String orderId, String address);
    public String createSessionForPaymentWithCallback(String orderId, String address, String callbackUrl);
    public void handleWebhookCallback(WebhookCallback callbackResult);
    public boolean chargeSofort(String orderId, String source);
    public boolean isStripeSecretActive();
    public void clearStripeSecret();
    public String getStripePublicKey();
}
