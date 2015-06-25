/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.braintree;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BrainTreeManager extends ManagerBase implements IBrainTreeManager {
 
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private CartManager cartManager;
    
    @Autowired
    private StoreApplicationPool storeApplicationPool;
    
    private BraintreeGateway getGateway() {
        Application braintreeApp = storeApplicationPool.getApplicationWithSecuredSettings("542e6a1e-9927-495c-9b6d-bb52af4ea9be");
        
        Setting setting = braintreeApp.settings.get("isSandBox");
        Environment env = null;
        
        if (setting != null && setting.value != null && setting.value.equals("true")) {
            env = Environment.SANDBOX;
        }
        
        if (setting != null && setting.value != null && setting.value.equals("false")) {
            env = Environment.PRODUCTION;
        }
        
        
        if (env == null) {
            return null;
        }
        
        String merchantid = braintreeApp.settings.get("merchantid").value;
        String publickey = braintreeApp.settings.get("publickey").value;
        String privatekey = braintreeApp.settings.get("privatekey").value;
        
        if (env == Environment.SANDBOX) {
            merchantid = braintreeApp.settings.get("sandbox_merchantid").value;
            publickey = braintreeApp.settings.get("sandbox_publickey").value;
            privatekey = braintreeApp.settings.get("sandbox_privatekey").value;    
        }
        
        return new BraintreeGateway(env, merchantid, publickey, privatekey); 
    }
    
    @Override
    public String getClientToken() {
        return getGateway().clientToken().generate();
    }

    @Override
    public boolean pay(String paymentMethodNonce, String orderId) {
        Order order = orderManager.getOrderSecure(orderId);
        if (order == null) {
            return false;
        }
        
        Application paymentApplication = storeApplicationPool.getApplication("542e6a1e-9927-495c-9b6d-bb52af4ea9be");
        if (paymentApplication != null) 
            order.changePaymentType(paymentApplication);
        
        
        double price = cartManager.calculateTotalCost(order.cart);
        if (order.shipping != null) {
            price += order.shipping.cost;
        }
        
        TransactionRequest request = new TransactionRequest()
        .amount(new BigDecimal(""+price))
        .paymentMethodNonce(paymentMethodNonce)
        .options()
            .submitForSettlement(true)
            .done();
        
        Result<Transaction> result = getGateway().transaction().sale(request);
        if (result.isSuccess()) {
            order.status = Order.Status.PAYMENT_COMPLETED;
            order.paymentTransactionId = result.getTarget().getId();
            orderManager.saveOrder(order);
            return true;
        } else {
            orderManager.changeOrderStatus(orderId, Order.Status.PAYMENT_FAILED);
            Transaction transaction = result.getTransaction();
            if (transaction != null) {
                System.out.println(""+transaction.getStatus());
                System.out.println(""+transaction.getProcessorResponseCode());
                System.out.println(""+transaction.getProcessorResponseText());
            }
            return false;
        }
    }
}
