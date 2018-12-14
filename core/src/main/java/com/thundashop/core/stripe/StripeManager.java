/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.stripe;

import com.getshop.scope.GetShopSession;
import com.stripe.Stripe;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hjemme
 */
@Component
@GetShopSession
public class StripeManager extends ManagerBase implements IStripeManager {

    @Autowired
    MessageManager messageManager;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    @Autowired
    UserManager userManager;
    
    @Override
    public boolean createAndChargeCustomer(String orderId, String token) {
        
        Order order = orderManager.getOrderSecure(orderId);
        if(storeManager.isProductMode()) {
            Application stripeApp = storeApplicationPool.getApplication("3d02e22a-b0ae-4173-ab92-892a94b457ae");
            Stripe.apiKey = stripeApp.getSetting("key");
        } else {
            Stripe.apiKey = "sk_test_BQokikJOvBiI2HlWgH4olfQ2";
        }

        try {
            // Create a Customer:
            User usr = userManager.getUserById(order.userId);
            String email = usr.emailAddress;
            if(email == null || email.isEmpty()) {
                email = "dummy@email.com";
            }
            
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("source", token);
            chargeParams.put("email", email);
            Customer customer = Customer.create(chargeParams);

            List<ExternalAccount> data = customer.getSources().getData();
            for(ExternalAccount account : data) {
                if(account.getObject().equals("card")) {
                    UserCard card = new UserCard();
                    Card stripecard = (Card)account;
                    card.expireMonth = stripecard.getExpMonth();
                    card.expireYear = stripecard.getExpYear();
                    card.card = customer.getId();
                    card.mask = stripecard.getLast4();
                    card.savedByVendor = "stripe";
                    card.id = stripecard.getId();
                    card.registeredDate = new Date();
                    userManager.addCardToUser(order.userId, card);
                }
            }
            
            return chargeOrder(orderId, null);
        }catch(Exception e) {
            logPrintException(e);
            messageManager.sendErrorNotification("Stripe integration exception", e);
        }
        return false;
    }

    @Override
    public boolean chargeOrder(String orderId, String cardId) {
        try {
            if(storeManager.isProductMode()) {
                Application stripeApp = storeApplicationPool.getApplication("3d02e22a-b0ae-4173-ab92-892a94b457ae");
                Stripe.apiKey = stripeApp.getSetting("key");
            } else {
                Stripe.apiKey = "sk_test_BQokikJOvBiI2HlWgH4olfQ2";
            }

            String currency = storeManager.getStoreSettingsApplicationKey("currencycode");
            if(currency == null || currency.isEmpty()) {
                currency = "NOK";
            }

            Order order = orderManager.getOrder(orderId);
            User user = userManager.getUserById(order.userId);
            Double amount = orderManager.getTotalAmount(order);
            for(UserCard card : user.savedCards) {
                if(cardId != null && !card.id.equals(cardId)) {
                    continue;
                } 
                if(card.savedByVendor != null && card.savedByVendor.equals("stripe")) {
                    Map<String, Object> customerParams = new HashMap<>();
                    customerParams.put("amount", (int)(amount * 100));
                    customerParams.put("currency", currency);
                    customerParams.put("customer", card.card);
                    try {
                        Charge charge = Charge.create(customerParams);
                        if(charge.getPaid()) {
                            orderManager.markAsPaid(orderId, new Date(), amount);
                            return true;
                        }
                    }catch(Exception d) {
                        logPrintException(d);
                    }
                }
            }
        }catch(Exception e) {
            logPrintException(e);
            messageManager.sendErrorNotification("Stripe integration exception", e);
        }
        return false;
    }
    
    public void saveCard(String card, Integer expMonth, Integer expYear) {
        if(storeManager.isProductMode()) {
            Application stripeApp = storeApplicationPool.getApplication("3d02e22a-b0ae-4173-ab92-892a94b457ae");
            Stripe.apiKey = stripeApp.getSetting("key");
        } else {
            Stripe.apiKey = "sk_test_BQokikJOvBiI2HlWgH4olfQ2";
        }

        try {
            // Create a Customer:
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("source", "tok_mastercard");
            chargeParams.put("email", "paying.user@example.com");
            Card stripecard = new Card();
            stripecard.setExpMonth(expMonth);
            stripecard.setExpYear(expYear);
            stripecard.setId(card);
            //Somehow put this into the source and create the customer.
            Customer customer = Customer.create(chargeParams);
        }catch(Exception e) {
            
        }
        
    }
    
}
