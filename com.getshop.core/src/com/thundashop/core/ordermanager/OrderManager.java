package com.thundashop.core.ordermanager;

import com.thundashop.core.appmanager.AppManager;
import com.thundashop.core.appmanager.data.ApplicationSubscription;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductVariation;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class OrderManager extends ManagerBase implements IOrderManager {

    public HashMap<String, Order> orders = new HashMap();
    @Autowired
    public MailFactory mailFactory;

    @Autowired
    public OrderManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataFromDatabase : data.data) {
            if (dataFromDatabase instanceof Order) {
                Order order = (Order) dataFromDatabase;
                orders.put(order.id, order);
            }
        }
    }

    private void saveOrderInternal(Order order) throws ErrorException {
        User user = getSession().currentUser;
        if (user != null) {
            order.userId = user.id;
        }

        order.storeId = storeId;
        databaseSaver.saveObject(order, credentials);
        orders.put(order.id, order);
    }

    private void updateStockQuantity(Order order, String key) throws ErrorException {
        HashMap<String, Setting> map = this.getSettings("StockControl");
        String setting = null;
        if (map != null) {
            setting = map.get(key).value;
        }

        if (setting != null && setting.equals("true")) {
//            for (Product product : order.cart.getProductList()) {
//                int factor = -1;
//                if (order.status == Order.Status.CANCELED) {
//                    factor = 1;
//                }
//
//                int change = order.cart.getProductCount(product) * factor;
//                ProductManager productManager = getManager(ProductManager.class);
//                productManager.changeStockQuantity(product.id, change);
//            }
        }
    }

    public String getCustomerOrderText(Order order) throws ErrorException {
        String newOrder = "Your order has been saved and will be processed by us as soon as possible";
        newOrder += "<br>";
        newOrder += "<br> <b>Order id:</b> " + order.id;
        newOrder += "<br>";
        newOrder += "<br> <b>Shipment information:</b>";
        newOrder += "<br> Name: " + order.cart.address.fullName;
        newOrder += "<br> Email: " + order.cart.address.emailAddress;
        newOrder += "<br> Address: " + order.cart.address.address;
        newOrder += "<br> Phone: " + order.cart.address.phone;
        newOrder += "<br> PostCode: " + order.cart.address.postCode + " " + order.cart.address.city;
        newOrder += "<br>";
        newOrder += "<br> <b>Items:</b> ";
        for (CartCompositeKey compKey : order.cart.products.keySet()) {
            Product product = order.cart.products.get(compKey);
            newOrder += "<br> " + order.cart.getProductCount(compKey) + "  x " + product.name;
            newOrder += "<br><b>You selected: </b>";
            for (String variation : compKey.getVariations()) {
                if (variation.equals("")) {
                    continue;
                }
                
                ProductVariation variation2 = product.getVariation(variation);
                newOrder += variation2.title+", ";
            }
            
            newOrder = newOrder.substring(0, newOrder.length() - 2);
            newOrder += "<br>";
        }

        return newOrder;
    }

    @Override
    public Order createOrder(Address address) throws ErrorException {
        CartManager cartManager = getManager(CartManager.class);
        Cart cart = cartManager.getCart();
        cart.address = address;
        
        Order order = new Order();
        order.createdDate = new Date();
        order.cart = cart;

        if (order.cart.address == null) {
            throw new ErrorException(53);
        }

        saveOrder(order);

        updateStockQuantity(order, "trackControl");

        Store store = this.getStore();
        String orderText = getCustomerOrderText(order);

        mailFactory.send(store.configuration.emailAdress, order.cart.address.emailAddress, "Thank you for your order", orderText);
        
        if (!store.configuration.emailAdress.equals(order.cart.address.emailAddress)) {
            mailFactory.send(store.configuration.emailAdress, store.configuration.emailAdress, "Thank you for your order", orderText);
        }

        return order;
    }

    @Override
    public List<Order> getOrders(ArrayList<String> orderIds, Integer page, Integer pageSize) throws ErrorException {
        User user = getSession().currentUser;
        List<Order> result = new ArrayList();
        for (Order order : orders.values()) {
            if (user.isAdministrator() || user.isEditor()) {
                result.add(order);
            } else if (order.userId.equals(user.id)) {
                result.add(order);
            }
        }

        Collections.sort(result);
        Collections.reverse(result);
        return result;
    }

    @Override
    public void saveOrder(Order order) throws ErrorException {
        saveOrderInternal(order);
    }

    @Override
    public void setOrderStatus(String password, String orderId, String currency, double price, int status) throws ErrorException {
        if (password.equals("asimpleButP0werfulPassw0rD")) {
            if (orderId.equals("applications")) {
                handleApplicationPayment(currency, price);
            } else {
                Order order = orders.get(orderId);
                if (order.cart.getTotal() == price) {
                    order.status = status;
                    saveOrderInternal(order);
                } else {
                    String content = "Hi.<br>";
                    content += "We received a payment notification from paypal for order: " + orderId + " which is incorrect.<br>";
                    content += "The price or the currency differ from what has been registered to the order.<br>";

                    String to = getStore().configuration.emailAdress;
                    mailFactory.send("post@getshop.com", to, "Possible fraud attempt", content);
                    mailFactory.send("post@getshop.com", "post@getshop.com", "Possible fraud attempt", content);
                }
            }
        } else {
            mailFactory.send("post@getshop.com", "post@getshop.com", "Status update failure", "tried to use password:" + password);
            
        }
    }

    private void handleApplicationPayment(String currency, double price) throws ErrorException {
        System.out.println(currency);
        AppManager appManager = getManager(AppManager.class);
        List<ApplicationSubscription> subscriptions = appManager.getUnpayedSubscription();
        double total = 0;
        for (ApplicationSubscription appsub : subscriptions) {
            if (appsub.app != null && appsub.app.price != null) {
                total += appsub.app.price;
            }
        }

        if (total == price && currency.equals("USD")) {
            appManager.renewAllApplications("fdder9bbvnfif909ereXXff");
        } else {
            String content = "Hi.<br>";
            content += "We received a payment notification from paypal for order for applications which is incorrect.<br>";
            content += "The price or the currency differ from what has been registered to the order <br>";
            content += "Price : " + price + " <br>";
            content += "Currency : " + currency + " <br>";

            String to = getStore().configuration.emailAdress;
            mailFactory.send("post@getshop.com", "post@getshop.com", "Application fraud attempt", content);
        }
    }
}
