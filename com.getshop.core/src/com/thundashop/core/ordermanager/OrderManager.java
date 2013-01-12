package com.thundashop.core.ordermanager;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.data.User;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class OrderManager extends ManagerBase implements IOrderManager {
    public HashMap<String, Order> orders = new HashMap<String, Order>();
    
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
        MessageBase messageBase = new MessageBase();
        messageBase.sessionId = getSession().id;
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
        if(map != null) {
            setting = map.get(key).value;
        }

        if (setting != null && setting.equals("true")) {
            for (Product product : order.cart.getProductList()) {
                int factor = -1;
                if (order.status == Order.Status.CANCELED) {
                    factor = 1;
                }

                int change = order.cart.getProductCount(product) * factor;
                ProductManager productManager = getManager(ProductManager.class);
                productManager.changeStockQuantity(product.id, change);
            }
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
        newOrder += "<br> PostCode: " + order.cart.address.postCode + " " + order.cart.address.city;
        newOrder += "<br>";
        newOrder += "<br> <b>Items:</b> ";
        for (Product product : order.cart.getProductList()) {
            newOrder += "<br> " + order.cart.getProductCount(product) + "  x " + product.name;
        }

        return newOrder;
    }

    @Override
    public Order createOrder(Cart cart) throws ErrorException {
        Order order = new Order();
        order.createdDate = new Date();
        order.cart = cart;

        if(order.cart.address == null) {
            throw new ErrorException(53);
        }
        
        saveOrder(order);

        updateStockQuantity(order, "trackControl");

        Store store = this.getStore();
        String orderText = getCustomerOrderText(order);

        mailFactory.send(store.configuration.emailAdress, order.cart.address.emailAddress, "Thank you for your order", orderText);
        mailFactory.send(store.configuration.emailAdress, store.configuration.emailAdress, "Thank you for your order", orderText);

        return order;
    }

    @Override
    public List<Order> getOrders(ArrayList<String> orderIds, Integer page, Integer pageSize) throws ErrorException {
        User user = getSession().currentUser;
        List<Order> result = new ArrayList();
        for(Order order : orders.values()) {
            if(user.isAdministrator() || user.isEditor()) {
                result.add(order);
            } else if(order.userId.equals(user.id)) {
                result.add(order);
            }
        }
        return result;
    }

    @Override
    public void saveOrder(Order order) throws ErrorException {
        orders.put(order.id, order);
        saveOrderInternal(order);
    }
}
