/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.stripe;

import com.getshop.scope.GetShopSession;
import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.ExternalAccount;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentSource;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.model.WebhookEndpoint;
import com.stripe.model.WebhookEndpointCollection;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opentravel.ota._2003._05.ActionType;
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
    
    private StripeSettings settings = new StripeSettings();
    
       @Override
    public synchronized void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof StripeSettings) {
                settings = (StripeSettings) dataCommon;
            }
        }
    }
    
    @Override
    public boolean createAndChargeCustomer(String orderId, String token) {
        
        Order order = orderManager.getOrderSecure(orderId);
        if(storeManager.isProductMode()) {
            Application stripeApp = storeApplicationPool.getApplication("3d02e22a-b0ae-4173-ab92-892a94b457ae");
            Stripe.apiKey = stripeApp.getSetting("key");
        } else {
            Stripe.apiKey = "sk_test_K7lzjnniaCB8MjTZjpodqriy";
        }

        try {
            // Create a Customer:
            User usr = userManager.getUserById(order.userId);
            String email = usr.emailAddress;
            if(email == null || !isValidEmail(email)) {
                email = "dummy@email.com";
            }
            
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("source", token);
            chargeParams.put("email", email);
            Customer customer = Customer.create(chargeParams);

            List<PaymentSource> data = customer.getSources().getData();
            saveCards(usr.id, customer.getId(), data);
            
            return chargeOrder(orderId, null);
        }catch(Exception e) {
            logPrintException(e);
            messageManager.sendErrorNotification("Stripe integration exception", e);
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
    
    public String createSessionForPayment(String orderId, String address) {
        try {
            
            if(storeManager.isProductMode()) {
                Application stripeApp = storeApplicationPool.getApplication("3d02e22a-b0ae-4173-ab92-892a94b457ae");
                Stripe.apiKey = stripeApp.getSetting("key");
            } else {
                Stripe.apiKey = "sk_test_K7lzjnniaCB8MjTZjpodqriy";
            }

            Order order = orderManager.getOrder(orderId);
            
            if(order == null) {
                return "";
            }
            
            User usr = userManager.getUserById(order.userId);
            String email = "";
            if(usr != null) {
                email = usr.emailAddress;
                if(email == null || !isValidEmail(email)) {
                    email = "no@emailset.com";
                }
            }
            
            String endpoints = createWebHook(Stripe.apiKey);
            
            String callback = getCallBackAddr();
            
            Map<String, Object> params = new HashMap<String, Object>();

            ArrayList<String> paymentMethodTypes = new ArrayList<>();
            paymentMethodTypes.add("card");
            params.put("payment_method_types", paymentMethodTypes);
            params.put("customer_email", email);
            params.put("client_reference_id", order.id);
            params.put("success_url", callback+"&page=payment_success&session_id={CHECKOUT_SESSION_ID}&orderid=" + orderId);
            params.put("cancel_url", callback+"&page=payment_failed&session_id={CHECKOUT_SESSION_ID}&orderid=" + orderId);
            
            
            List<String> supportedLanguages = Arrays.asList("da", "de", "en", "es", "fi", "fr", "it", "ja", "nb", "nl", "pl", "pt", "sv", "zh");
            
            
            if(order.language != null && !order.language.isEmpty()) {
                if(supportedLanguages.contains(order.language.toLowerCase())) {
                    params.put("locale",order.language);
                } else if(order.language.equalsIgnoreCase("no")) {
                    params.put("locale","nb");
                }
            }
            
//            params.put("locale", "nb");

            String currency = storeManager.getStoreSettingsApplicationKey("currencycode");
            if(currency == null || currency.isEmpty()) {
                currency = "NOK";
            }
            HashMap<String, Object> lineItem = new HashMap<String, Object>();
            lineItem.put("name", "Payment");
            lineItem.put("description", "Payment for order " + order.incrementOrderId);
            int total = (int)(orderManager.getTotalAmount(order)*100);
            if(total <= 0) {
                return "";
            }
            lineItem.put("amount", total);
            lineItem.put("currency", currency);
            lineItem.put("quantity", 1);

            ArrayList<HashMap<String, Object>> lineItems = new ArrayList<>();
            lineItems.add(lineItem);
            params.put("line_items", lineItems);
            
            Session session = Session.create(params);
            
            order.payment.transactionLog.put(System.currentTimeMillis(), "Transferred to payment window");
            orderManager.saveOrder(order);
            
            return session.getId();
        }catch(Exception e) {
            messageManager.sendErrorNotification("Error in new payment stripe integration", e);
            logPrint(e.getMessage());
        }
        return "";
    }
    
    @Override
    public boolean chargeSofort(String orderId, String source) {
        try {
            if(storeManager.isProductMode()) {
                Application stripeApp = storeApplicationPool.getApplication("3d02e22a-b0ae-4173-ab92-892a94b457ae");
                Stripe.apiKey = stripeApp.getSetting("key");
            } else {
                Stripe.apiKey = "sk_test_K7lzjnniaCB8MjTZjpodqriy";
            }

            String currency = storeManager.getStoreSettingsApplicationKey("currencycode");
            if(currency == null || currency.isEmpty()) {
                currency = "NOK";
            }

            Order order = orderManager.getOrderSecure(orderId);
            User user = userManager.getUserById(order.userId);
            Double amount = orderManager.getTotalAmount(order);
            Map<String, Object> customerParams = new HashMap<>();
            customerParams.put("amount", (int)(amount * 100));
            customerParams.put("currency", currency);
            customerParams.put("source", source);
            try {
                Charge charge = Charge.create(customerParams);
                order.payment.transactionLog.put(System.currentTimeMillis(), "Trying to charge card using sofort source" + source);
                if(charge.getCaptured()) {
                    orderManager.markAsPaid(orderId, new Date(), amount);
                    return true;
                } else {
                    order.status = Order.Status.PAYMENT_FAILED;
                    orderManager.saveOrder(order);
                }
            }catch(Exception d) {
                order.payment.transactionLog.put(System.currentTimeMillis(), "Failed to charge card: " + d.getMessage());
                logPrintException(d);
            }
            orderManager.saveOrderInternal(order);
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
                Stripe.apiKey = "sk_test_K7lzjnniaCB8MjTZjpodqriy";
            }

            String currency = storeManager.getStoreSettingsApplicationKey("currencycode");
            if(currency == null || currency.isEmpty()) {
                currency = "NOK";
            }

            Order order = orderManager.getOrderSecure(orderId);
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
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Trying to charge card: " + card.mask + ", " + card.card + ", " + card.id);
                        if(charge.getPaid()) {
                            orderManager.markAsPaid(orderId, new Date(), amount);
                            return true;
                        } else {
                            order.status = Order.Status.PAYMENT_FAILED;
                            orderManager.saveOrder(order);
                        }
                    }catch(Exception d) {
                        order.payment.transactionLog.put(System.currentTimeMillis(), "Failed to charge card: " + d.getMessage());
                        logPrintException(d);
                    }
                }
            }
            orderManager.saveOrderInternal(order);
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
            Stripe.apiKey = "sk_test_K7lzjnniaCB8MjTZjpodqriy";
        }

        try {
            // Create a Customer:
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("source", "tok_mastercard");
            chargeParams.put("email", "paying.user@example.com");
            Card stripecard = new Card();
            stripecard.setExpMonth((long)expMonth);
            stripecard.setExpYear((long)expYear);
            stripecard.setId(card);
            //Somehow put this into the source and create the customer.
            Customer customer = Customer.create(chargeParams);
        }catch(Exception e) {
            
        }
    }
    
    public void handleWebhookCallback(WebhookCallback result) {
        
        if(storeManager.isProductMode()) {
            Application stripeApp = storeApplicationPool.getApplication("3d02e22a-b0ae-4173-ab92-892a94b457ae");
            Stripe.apiKey = stripeApp.getSetting("key");
        } else {
            Stripe.apiKey = "sk_test_K7lzjnniaCB8MjTZjpodqriy";
        }
        
        Order order = orderManager.getOrderDirect(result.orderId);
        if(order==null) { return; }
        
        try {
           result.payload = new String(Base64.getDecoder().decode(result.payload.getBytes()));
           Event event = Webhook.constructEvent(result.payload, result.validationKey, settings.webhookSecret);
        } catch (JsonSyntaxException e) {
            order.payment.transactionLog.put(System.currentTimeMillis(), "Failed to handle webhook callback from stripe on payload" + result.payload);
            orderManager.saveOrder(order);
            return;
        } catch (SignatureVerificationException e) {
            order.payment.transactionLog.put(System.currentTimeMillis(), "Failed to handle webhook callback from stripe on payload (invalid signature)" + result.payload);
            orderManager.saveOrder(order);
            return;
        }
        
        order.payment.callBackParameters = result.result;
        orderManager.saveOrder(order);
        orderManager.markAsPaid(result.orderId, new Date(), (double)result.amount/100);
        
        try {
            //Saving the card
            String customerId = result.result.get("customer");
            Map<String, Object> cardParams = new HashMap<String, Object>();
            cardParams.put("limit", 3);
            cardParams.put("customer", customerId);
            cardParams.put("type", "card");
            List<PaymentMethod> cards = PaymentMethod.list(cardParams).getData();
            saveCardByPaymentMethod(order.userId, customerId, cards);
        } catch (StripeException ex) {
            
            Logger.getLogger(StripeManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    

    private String createWebHook(String key) {
        try {
            Stripe.apiKey = key;
            String webhookaddress = getCallBackAddr();
            
            boolean createWebHook = settings.webhookSecret.isEmpty();
            
            if(createWebHook) {
                Map<String, Object> webhookendpointParams = new HashMap<String, Object>();
                webhookendpointParams.put("url", webhookaddress);
                webhookendpointParams.put("enabled_events", Arrays.asList("checkout.session.completed"));

                WebhookEndpoint result = WebhookEndpoint.create(webhookendpointParams);

                String secret = result.getSecret();
                settings.webhookSecret = secret;
                saveObject(settings);
            }

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("limit", "30");
            WebhookEndpointCollection webhooks = WebhookEndpoint.list(params);
            List<WebhookEndpoint> webhooksList = webhooks.getData();
            String endpointsCreated = "";
            for(WebhookEndpoint endpoint : webhooksList) {
                endpointsCreated += endpoint.getUrl() + "<br>";
            }
            
            return endpointsCreated;
        }catch(Exception e) {
            logPrint(e.getMessage());
        }
        return "";
    }

    private void saveCards(String userId, String customerId, List<PaymentSource> data) {
        for(PaymentSource account : data) {
            if(account instanceof Card) {
                UserCard card = new UserCard();
                Card stripecard = (Card)account;
                card.expireMonth = new Integer(stripecard.getExpMonth()+"");
                card.expireYear = new Integer(stripecard.getExpYear()+"");
                card.card = customerId;
                card.mask = stripecard.getLast4();
                card.savedByVendor = "stripe";
                card.id = stripecard.getId();
                card.registeredDate = new Date();
                userManager.addCardToUser(userId, card);
            }
        }
    }
    
    private void saveCardByPaymentMethod(String userId, String customerId, List<PaymentMethod> data) {
        for(PaymentMethod account : data) {
            UserCard card = new UserCard();
            PaymentMethod.Card stripecard = account.getCard();
            card.expireMonth = new Integer(stripecard.getExpMonth()+"");
            card.expireYear = new Integer(stripecard.getExpYear()+"");
            card.card = customerId;
            card.mask = stripecard.getLast4();
            card.savedByVendor = "stripe";
            card.id = account.getId();
            card.registeredDate = new Date();
            userManager.addCardToUser(userId, card);
        }
        
    }

    private String getCallBackAddr() {
        if(!storeManager.isProductMode()) {
            return "http://20360.3.0.mdev.getshop.com/callback.php?useapp=stripe";
        }

        String address = "";
        String mainaddress = storeManager.getMyStore().getDefaultWebAddress();
        if(mainaddress.contains("getshop.com")) {
            address = "https://" + mainaddress + "/callback.php?useapp=stripe";
        } else {
            for(String tmpAddr : storeManager.getMyStore().additionalDomainNames) {
                if(tmpAddr.contains("getshop.com")) {
                    address = "https://" + tmpAddr + "/callback.php?useapp=stripe";
                    break;
                }
            }
        }
        
        return address;
    }
    
}
