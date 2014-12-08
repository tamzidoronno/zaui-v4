package com.thundashop.core.ordermanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Component
@GetShopSession
public class OrderManager extends ManagerBase implements IOrderManager {
    private long incrementingOrderId = 100000;
    
    public HashMap<String, Order> orders = new HashMap();
   
    @Autowired
    public MailFactory mailFactory;

    @Autowired
    private UserManager userManager;
    
    @Autowired
    private StoreManager storeManager;
    
    @Autowired
    private ProductManager productManager;
    
    @Autowired
    private CartManager cartManager;
    
    @Autowired
    private PageManager pageManager;
    
    @Autowired
    private StoreApplicationInstancePool storeApplicationInstancePool;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataFromDatabase : data.data) {
            if (dataFromDatabase instanceof Order) {
                Order order = (Order) dataFromDatabase;
                if (order.cart == null || order.cart.address == null) {
                    try {
                        System.out.println("Removing order: " + order.id + " due to incorrect data on them");
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
        
        printTest();
    }
    
    private void printTest() {
        int i = 0;
        while(i<10) {
            double weekData = 0;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2014);
            cal.set(Calendar.DAY_OF_YEAR, i*28);
            Date start = cal.getTime();
            cal.set(Calendar.DAY_OF_YEAR, ((i*28)+28));
            Date end = cal.getTime();
            for (Order order : orders.values()) {
               if (order.rowCreatedDate.after(start) && order.rowCreatedDate.before(end)) {
                   weekData += cartManager.calculateTotalCost(order.cart);
               }
            }
            System.out.print(weekData+",");
            i++;
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

    private HashMap<String, Setting> getSettings(String phpApplicationName) throws ErrorException {
        return storeApplicationInstancePool.getApplicationInstanceSettingsByPhpName(phpApplicationName);
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
        text = text.replace("/displayImage", "http://"+storeManager.getMyStore().webAddress+"/displayImage");
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
            if (value != null)
                return value;
        }
        
        return "Thank you for your order";
    }
    
    @Override
    public Order createOrder(Address address) throws ErrorException {
        Order order = createOrderInternally(address);
        saveOrder(order);
        updateStockAndSendConfirmation(order);
        return order;
    }
    
    
    @Override
    public Order createOrderByCustomerReference(String referenceKey) throws ErrorException {
        User user = userManager.getUserByReference(referenceKey);
        user.address.phone = user.cellPhone;
        user.address.fullName = user.fullName;
        Order order = createOrderInternally(user.address);
        order.userId = user.id;
        saveOrder(order);
        updateStockAndSendConfirmation(order);
        return order;
    }

    @Override
    public List<Order> getOrders(ArrayList<String> orderIds, Integer page, Integer pageSize) throws ErrorException {
        User user = getSession().currentUser;
        List<Order> result = new ArrayList();
        for (Order order : orders.values()) {
            if (orderIds != null && orderIds.size() > 0) {
                if (!orderIds.contains(order.id)) {
                    continue;
                }
            }
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
        
        if (page != null && pageSize != null) {
            int from = (page-1)*pageSize;
            int to = pageSize*page;
            
            if (to > result.size()) {
                to = result.size();
            }
            
            try {
                List<Order> retOrders = result.subList(from, to);
                return new ArrayList<Order>(retOrders);
            } catch (IllegalArgumentException ex) {
                return new ArrayList();
            }
        }
        
        return result;
    }

    @Override
    public void saveOrder(Order order) throws ErrorException {
        saveOrderInternal(order);
    }

    @Override
    public void setOrderStatus(String password, String orderId, String currency, double price, int status) throws ErrorException {
        if (password.equals("1Fuck1nG_H4T3_4ppl3!!TheySuckBigTime")) {
            Order order = orders.get(orderId);

            if (order.cart.getTotal(false) == price) {
                changeOrderStatus(order.id, status);
            } else {
                String content = "Hi.<br>";
                content += "We received a payment notification from paypal for order: " + orderId + " which is incorrect.<br>";
                content += "The price or the currency differ from what has been registered to the order.<br>";

                String to = storeManager.getMyStore().configuration.emailAdress;
                mailFactory.send("post@getshop.com", to, "Possible fraud attempt", content);
                mailFactory.send("post@getshop.com", "post@getshop.com", "Possible fraud attempt", content);
            }
        } else {
            mailFactory.send("post@getshop.com", "post@getshop.com", "Status update failure", "tried to use password:" + password);
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
        cartManager.updateCoupons(order.cart.coupon);
    }
    
    @Override
    public List<CartTax> getTaxes(Order order) throws ErrorException {
        return order.cart.getCartTaxes();
    }

    private Order createOrderInternally(Address address) throws ErrorException {
        Cart cart = cartManager.getCart();
        cart.address = address;

        Order order = new Order();
        order.createdDate = new Date();
        order.cart = cart.clone();
        order.reference = cart.reference;

        if (order.cart == null || order.cart.address == null) {
            throw new ErrorException(53);
        }
        
        finalizeCart(order.cart);
        
        incrementingOrderId++;
        order.incrementOrderId = incrementingOrderId;
        return order;
    }

    private void updateStockAndSendConfirmation(Order order) throws ErrorException {

        updateStockQuantity(order, "trackControl");
        updateCouponsCount(order);
        
        Store store = storeManager.getMyStore();
        String orderText = getCustomerOrderText(order);

        String subject = getSubject();
        HashMap<String, Setting> settings = getSettings("Settings");
        
        if(settings != null && settings.containsKey("stoporderemail") && settings.get("stoporderemail").value.equals("true")) {
            return;
        }
        
        if(!subject.isEmpty()) {
            mailFactory.send(store.configuration.emailAdress, order.cart.address.emailAddress, getSubject() , orderText);

            if (store.configuration.emailAdress != null && !store.configuration.emailAdress.equals(order.cart.address.emailAddress)) {
                mailFactory.send(store.configuration.emailAdress, store.configuration.emailAdress, getSubject(), orderText);
            }
        }
    }

    @Override
    public Order getOrderByReference(String referenceId) throws ErrorException {
        for(Order order : orders.values()) {
            if(order.reference.equals(referenceId)) {
                return order;
            }
        }
        return null;
    }

    @Override
    public List<Order> getAllOrdersForUser(String userId) throws ErrorException {
        User user = getSession().currentUser;
        List<Order> returnOrders = new ArrayList();
        for(Order order : orders.values()) {
            if((order.userId != null && order.userId.equals(userId)) || (user != null && user.isAdministrator())) {
                returnOrders.add(order);
            }
        }
        return returnOrders;
    }

    @Override
    public int getPageCount(int pageSize, String searchWord) {
        List<Order> orders = null;
        if (searchWord != null) {
            orders = searchForOrders(searchWord, null, null);
        } else {
            orders = getOrders(null, null, null);
        }
         
        if (orders.size() == 0) {
            return 1;
        }
        
        return (int) Math.ceil((double)orders.size()/(double)pageSize);
     }

    @Override
    public List<Order> searchForOrders(String searchWord, Integer page, Integer pageSize) {
        String[] inSearchWords = searchWord.split(" ");
        
        Set<String> orderIds = new HashSet();
        
        for (String search : inSearchWords) {
            String searchLower = search.toLowerCase();
            
            // add orders with name
            orders.values().stream()
                    .filter(o -> o.cart != null)
                    .filter(o -> o.cart.address != null)
                    .filter(o -> o.cart.address.fullName != null)
                    .filter(o -> o.cart.address.fullName.toLowerCase().contains(searchLower))
                    .forEach(o -> orderIds.add(o.id));
            
            if (isInteger(searchLower)) {
                // Add orders that has the integer
                orders.values().stream()
                        .filter(o -> o.incrementOrderId == Integer.valueOf(searchLower))
                        .forEach(o -> orderIds.add(o.id));
            }
        }
        
        ArrayList<String> listOrderIds = new ArrayList(orderIds);
        
        if (listOrderIds.size() == 0) {
            return new ArrayList<Order>();
        }
        
        return getOrders(listOrderIds, page, pageSize);
        
    }

    private boolean isInteger(String search) {
        try { 
            Integer.parseInt(search); 
        } catch(NumberFormatException e) { 
            return false; 
        }
        // only got here if we didn't return false
        return true;
    }

    @Override
    public double getTotalSalesAmount(Integer year) {
        double amount = 0;
        for (Order order : orders.values()) {
            if (year != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(order.createdDate);
                if (cal.get(Calendar.YEAR) != year) {
                    continue;
                }
            }
            
            amount += cartManager.calculateTotalCost(order.cart);
        }
        
        return amount;
    }

    
}
