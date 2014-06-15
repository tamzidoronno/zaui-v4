package com.thundashop.core.ordermanager;

import com.thundashop.core.appmanager.AppManager;
import com.thundashop.core.appmanager.data.ApplicationSubscription;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
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
    private long incrementingOrderId = 100000;
    
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
                if (order.shipping == null || order.cart == null || order.cart.address == null) {
                    try {
                        databaseSaver.deleteObject(dataFromDatabase, credentials);
                    } catch (ErrorException ex) {
                        ex.printStackTrace();
                    }
                    continue;
                }
                if (order.incrementOrderId > incrementingOrderId) {
                    incrementingOrderId = order.incrementOrderId;
                }
                orders.put(order.id, order);
            }
        }
    }

    private void saveOrderInternal(Order order) throws ErrorException {
        User user = getSession().currentUser;
        if (user != null && order.userId == null) {
            order.userId = user.id;
        }
        order.session = getSession().id;

        order.storeId = storeId;
        databaseSaver.saveObject(order, credentials);
        orders.put(order.id, order);
    }

    private void updateStockQuantity(Order order, String key) throws ErrorException {
        HashMap<String, Setting> map = this.getSettings("StockControl");
        String setting = null;
        if (map != null && map.containsKey(key)) {
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
    
    private String formatText(Order order, String text) throws ErrorException {
        text = text.replace("/displayImage", "http://"+getStore().webAddress+"/displayImage");
        text = text.replace("{Order.Id}", order.id);
        text = text.replace("{Order.Lines}", getOrderLines(order));
        
        if (order.cart.address.fullName != null)
            text = text.replace("{Customer.Name}", order.cart.address.fullName);
        
        if (order.cart.address.emailAddress != null)
            text = text.replace("{Customer.Email}", order.cart.address.emailAddress);
        
        if (order.cart.address.address != null)
            text = text.replace("{Customer.Address}", order.cart.address.address);
        
        if (order.cart.address.city != null)
            text = text.replace("{Customer.City}", order.cart.address.city);
        
        if (order.cart.address.phone != null)
            text = text.replace("{Customer.Phone}", order.cart.address.phone);
        
        if (order.cart.address.postCode != null)
            text = text.replace("{Customer.Postcode}", order.cart.address.postCode);
        
        return text;
    }
    
    public String getCustomerOrderText(Order order) throws ErrorException {
        HashMap<String, Setting> settings = getSettings("MailManager");
        if (settings != null && settings.get("ordermail") != null) {
            Setting setting = settings.get("ordermail");
            String value = setting.value;
            if (value != null && !value.equals("")) {
                return formatText(order, value);
            }
        }
        return getDefaultOrderText(order);
    }
    
    private String getOrderLines(Order order) {
        String newOrder = "";
        for (CartItem cartItem : order.cart.getItems()) {
            Product product = cartItem.getProduct();
            newOrder += cartItem.getCount() + "  x " + product.name;

            if (cartItem.getVariations().size() > 0) {
                newOrder += " (";
                for (String variation : cartItem.getVariations()) {
                    if (variation.equals("")) {
                        continue;
                    }

                    newOrder += product.getVariation(variation).title + ", ";
                }
                newOrder = newOrder.substring(0, newOrder.length() - 2) + ")";
            }

            newOrder += "<br>";
        }
        return newOrder;
    }
    
    private String getDefaultOrderText(Order order) throws ErrorException {
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
        if(order.cart.address.countryname != null && !order.cart.address.countryname.isEmpty()) {
            newOrder += "<br> Country: " + order.cart.address.countryname;
        }
        newOrder += "<br>";
        newOrder += "<br> <b>Items:</b> ";
        newOrder += getOrderLines(order);
        return newOrder;
    }
    
     public void finalizeCart(Cart cart) throws ErrorException {
        ProductManager productManager = getManager(ProductManager.class);
        
        for (CartItem item : cart.getItems()) {
            double price = productManager.getPrice(item.getProduct().id, item.getVariations());
            item.getProduct().price = price;
        }
    }

    private String getSubject() throws ErrorException {
        HashMap<String, Setting> settings = getSettings("MailManager");
        if (settings != null && settings.get("ordermail_subject") != null) {
            Setting setting = settings.get("ordermail_subject");
            String value = setting.value;
            if (value != null && !value.equals(""))
                return value;
        }
        
        return "Thank you for your order";
    }
    
    @Override
    public Order createOrder(Address address) throws ErrorException {
        CartManager cartManager = getManager(CartManager.class);
        Cart cart = cartManager.getCart();
        cart.address = address;

        Order order = new Order();
        order.createdDate = new Date();
        order.cart = cart.clone();

        if (order.cart == null || order.cart.address == null) {
            throw new ErrorException(53);
        }
        
        finalizeCart(order.cart);
        
        incrementingOrderId++;
        order.incrementOrderId = incrementingOrderId;
        saveOrder(order);

        updateStockQuantity(order, "trackControl");
        updateCouponsCount(order);
        
        Store store = this.getStore();
        String orderText = getCustomerOrderText(order);

        mailFactory.send(store.configuration.emailAdress, order.cart.address.emailAddress, getSubject() , orderText);

        if (store.configuration.emailAdress != null && !store.configuration.emailAdress.equals(order.cart.address.emailAddress)) {
            mailFactory.send(store.configuration.emailAdress, store.configuration.emailAdress, getSubject(), orderText);
        }

        return order;
    }

    @Override
    public List<Order> getOrders(ArrayList<String> orderIds, Integer page, Integer pageSize) throws ErrorException {
        User user = getSession().currentUser;
        List<Order> result = new ArrayList();
        for (Order order : orders.values()) {
            if (user == null) {
                if (order.session != null && order.session.equals(getSession().id)) {
                    result.add(order);
                }
            } else if (user.isAdministrator() || user.isEditor()) {
                result.add(order);
            } else if (order.userId != null && order.userId.equals(user.id)) {
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
        if (password.equals("1Fuck1nG_H4T3_4ppl3!!TheySuckBigTime")) {
            if (orderId.equals("applications")) {
                handleApplicationPayment(currency, price);
            } else {
                Order order = orders.get(orderId);
                
                if (order.cart.getTotal(false) == price) {
                    changeOrderStatus(order.id, status);
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

    @Override
    public Order getOrder(String orderId) throws ErrorException {
        User user = getSession().currentUser;
        for (Order order : orders.values()) {
            if(!order.id.equals(orderId)) {
                continue;
            }
            if (user == null) {
                if (order.session != null && order.session.equals(getSession().id)) {
                    return order;
                }
            } else if (user.isAdministrator() || user.isEditor()) {
                return order;
            } else if (order.userId.equals(user.id)) {
                return order;
            }
        }
        
        throw new ErrorException(61);
    }

    private Order getByTransactionId(String transactionId) {
        for (Order order : orders.values()) {
            if (order.paymentTransactionId.equals(transactionId)) {
                return order;
            }
        }
        
        return null;
    }

    @Override
    public void changeOrderStatus(String id, int status) throws ErrorException {
        Order order = orders.get(id);
        if (order == null) {
            order = getByTransactionId(id);
        }
        
        if (order != null) {
            order.status = status;
            saveOrderInternal(order);
        }
    }

    @Override
    public Order getOrderByincrementOrderId(Integer id) throws ErrorException {
        for(Order order : orders.values()) {
            if(order.incrementOrderId == id) {
                return order;
            }
        }
        throw new ErrorException(61);
    }

    @Override
    public Double getTotalAmount(Order order) {
        Double toPay = order.cart.getTotal(false);
        
        if (order.shipping != null && order.shipping.cost > 0) {
            toPay += order.shipping.cost;
        }
        
        if (order.payment != null && order.payment.paymentFee > 0) {
            toPay += order.payment.paymentFee;
        }
        
        if (toPay < 0) {
            toPay = 0D;
        }
        
        return toPay;
    }

    private void updateCouponsCount(Order order) throws ErrorException {
        CartManager cartManager = getManager(CartManager.class);
        cartManager.updateCoupons(order.cart.coupon);
    }
    
    @Override
    public List<CartTax> getTaxes(Order order) throws ErrorException {
        return order.cart.getCartTaxes();
    }
}
