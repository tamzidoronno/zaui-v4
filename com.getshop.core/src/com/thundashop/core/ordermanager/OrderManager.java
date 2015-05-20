package com.thundashop.core.ordermanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.hotelbookingmanager.Statistics;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.SalesStats;
import com.thundashop.core.ordermanager.data.Statistic;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductDynamicPrice;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
    
    @Autowired
    private StoreApplicationPool storeApplicationPool;
    
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
        
    }
    

    private void saveOrderInternal(Order order) throws ErrorException {
        
        validatePaymentStatus(order);
        
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
    
    @Override
    public void logTransactionEntry(String orderId, String entry) throws ErrorException {
        Order order = getOrder(orderId);
        order.payment.transactionLog.put(new Date().getTime(), entry);
        saveOrder(order);
    }

    private String formatText(Order order, String text) throws ErrorException {
        text = text.replace("\n", "<br/>");
//        text = text.replace("/displayImage", "http://" + storeManager.getMyStore().webAddress + "/displayImage");
        text = text.replace("{Order.Id}", order.id);
        text = text.replace("{Order.IncrementalOrderId}", ""+order.incrementOrderId);
        text = text.replace("{Order.Lines}", getOrderLines(order));
        
        if (order.cart.address.fullName != null) {
            text = text.replace("{Customer.Name}", order.cart.address.fullName);
        }
        
        if (order.cart.address.emailAddress != null) {
            text = text.replace("{Customer.Email}", order.cart.address.emailAddress);
        }
        
        if (order.cart.address.address != null) {
            text = text.replace("{Customer.Address}", order.cart.address.address);
        }
        
        if (order.cart.address.city != null) {
            text = text.replace("{Customer.City}", order.cart.address.city);
        }
        
        if (order.cart.address.phone != null) {
            text = text.replace("{Customer.Phone}", order.cart.address.phone);
        }
        
        if (order.cart.address.postCode != null) {
            text = text.replace("{Customer.Postcode}", order.cart.address.postCode);
        }
        
        return text;
    }
    
    private String getCustomerOrderText(Order order, String textTemplate) throws ErrorException {
        if (textTemplate != null && !textTemplate.isEmpty()) {
            return formatText(order, textTemplate);
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
        if (order.cart.address.countryname != null && !order.cart.address.countryname.isEmpty()) {
            newOrder += "<br> Country: " + order.cart.address.countryname;
        }
        newOrder += "<br>";
        newOrder += "<br> <b>Items:</b> ";
        newOrder += getOrderLines(order);
        return newOrder;
    }
    
    public void finalizeCart(Cart cart) throws ErrorException {
        User user = getSession() != null && getSession().currentUser != null ? getSession().currentUser : null;
        
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            double price = productManager.getPrice(item.getProduct().id, item.getVariations());
            product.price = price;
            
            if (user != null && user.discount > 0) {
                product.price = getPriceBasedOnUserDiscount(item, user);
            } else if (product.prices != null && product.prices.size() > 0) {
                if (product.progressivePriceModel) {
                    product.price = getPriceBasedOnProgressiveModel(item);
                } else {
                    product.price = getPriceBasedOnCount(item);
                }
                
                product.original_price = productManager.getPriceWithoutDiscount(product.id, item.getVariations());
            }
            
            
            if(new Double(product.price).isNaN()) {
                product.price = 0;
            }
        }
    }

    @Override
    public Order createOrder(Address address) throws ErrorException {
        Order order = createOrderInternally(address);
        saveOrder(order);
        updateStockAndSendConfirmation(order);
        return order;
    }

    @Override
    public Order createOrderForUser(String userId) {
        User user = userManager.getUserById(userId);
        if (user == null) {
            throw new ErrorException(26);
        }
            
        Address address = user.address;
        Order order = createOrderInternally(address);
        order.userId = user.id;
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
            int from = (page - 1) * pageSize;
            int to = pageSize * page;
            
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
            if (!order.id.equals(orderId)) {
                continue;
            }
            String currentSession = getSession().id;
            if (user == null) {
                if (order.session != null && order.session.equals(currentSession)) {
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
        for (Order order : orders.values()) {
            if (order.incrementOrderId == id) {
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
        
        //What about orders that is not supposed to be sent, why an address then?
//        if (order.cart == null || order.cart.address == null) {
//            throw new ErrorException(53);
//        }
        
        finalizeCart(order.cart);
        
        incrementingOrderId++;
        order.incrementOrderId = incrementingOrderId;
        return order;
    }
    
    private void updateStockAndSendConfirmation(Order order) throws ErrorException {
        
        updateStockQuantity(order, "trackControl");
        updateCouponsCount(order);
        
        Application orderManagerApplication = storeApplicationPool.getApplication("27716a58-0749-4601-a1bc-051a43a16d14");
        if (!orderManagerApplication.getSetting("shouldSendEmail").equals("true")) {
            return;
        }
        
        Store store = storeManager.getMyStore();
        String orderText = getCustomerOrderText(order, orderManagerApplication.getSetting("orderemail"));
       
        String subject = orderManagerApplication.getSetting("ordersubject");
        if (subject.isEmpty()) {
            subject = "Thanks for your order";
        }
        
        subject = formatText(order, subject);
        
        HashMap<String, Setting> settings = getSettings("Settings");
        
        if (settings != null && settings.containsKey("stoporderemail") && settings.get("stoporderemail").value.equals("true")) {
            return;
        }
        
        if (!subject.isEmpty()) {
            mailFactory.send(store.configuration.emailAdress, order.cart.address.emailAddress, subject, orderText);
            
            if (store.configuration.emailAdress != null && !store.configuration.emailAdress.equals(order.cart.address.emailAddress)) {
                mailFactory.send(store.configuration.emailAdress, store.configuration.emailAdress, subject, orderText);
            }
        }
    }
    
    @Override
    public Order getOrderByReference(String referenceId) throws ErrorException {
        for (Order order : orders.values()) {
            if (order.reference.equals(referenceId)) {
                return order;
            }
        }
        return null;
    }
    
    @Override
    public List<Order> getAllOrdersForUser(String userId) throws ErrorException {
        User user = getSession().currentUser;
        List<Order> returnOrders = new ArrayList();
        for (Order order : orders.values()) {
            if ((order.userId != null && order.userId.equals(userId)) || (user != null && user.isAdministrator())) {
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
        
        return (int) Math.ceil((double) orders.size() / (double) pageSize);
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
        } catch (NumberFormatException e) {            
            return false;            
        }
        // only got here if we didn't return false
        return true;
    }
    
    @Override
    public Double getTotalSalesAmount(Integer year, Integer month, Integer week, Integer day, String type) {
        double amount = 0;
        for (Order order : orders.values()) {
            
            if (!order.useForStatistic()) {
                continue;
            }
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.createdDate);
            if (year != null && cal.get(Calendar.YEAR) != year) {
                continue;
            }
            if (month != null && cal.get(Calendar.MONTH) != (month-1)) {
                continue;
            }
            if (week != null && cal.get(Calendar.WEEK_OF_YEAR) != week) {
                continue;
            }
            if (day != null && cal.get(Calendar.DAY_OF_YEAR) != day) {
                continue;
            }
            
            if(type != null) {
                if(order.payment != null && order.payment.paymentType != null && !order.payment.paymentType.equals(type)) {
                    continue;
                }
            }
            
            amount += cartManager.calculateTotalCost(order.cart);
        }

        return amount;
    }
    
    @Override
    public Map<String, List<Statistic>> getMostSoldProducts(int numberOfProducts) {
        Map<String, Integer> counts = new HashMap();
        
        for (Order order : orders.values()) {
            for (CartItem item : order.cart.getItems()) {
                Integer oldCount = counts.get(item.getProduct().id);
                if (oldCount == null) {
                    oldCount = 0;
                }
                
                oldCount += item.getCount();
                counts.put(item.getProduct().id, oldCount);
            }
        }
        
        counts = sortByValue(counts);
        
        Map<String, List<Statistic>> retMap = new HashMap();
        int i = 0;
        for (String productId : counts.keySet()) {
            List<Statistic> statistics = new ArrayList();
            statistics.addAll(createStatistic(productId));
            
            retMap.put(productId, statistics);
            
            i++;
            if (i >= numberOfProducts) {
                break;
            }
            
            
        }
        
        return retMap;
    }
    
    private Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        
        Collections.reverse(list);
        
        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }    

    private List<Statistic> createStatistic(String productId) {
        List<Statistic> statistics = new ArrayList();
        
        int yearsBack = 3;
        
        int j = 0;
        int month = 0;
        while (month < 12 && j < yearsBack) {
            month++;
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, (j*-1));
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, month);
            Date startTime = calendar.getTime();
            
            calendar.set(Calendar.MONTH, (month+1));
            Date endTime = calendar.getTime();
            
            Calendar yearCalendar = Calendar.getInstance();
            yearCalendar.setTime(new Date());
            yearCalendar.add(Calendar.YEAR, j*-1);
            int year = yearCalendar.get(Calendar.YEAR);
            
            int count = 0;
            for (Order order : orders.values()) {
                if (order.cart != null && order.cart.getItems() != null) {
                    for (CartItem item : order.cart.getItems()) {
                        if (item.getProduct().id.equals(productId) && order.createdDate.after(startTime) && order.createdDate.before(endTime)) {
                            count += item.getCount();
                        }
                    }
                }
            }
            
            Statistic statistic = new Statistic();
            
            statistic.count = count;
            statistic.id = productId;
            statistic.month = month;
            statistic.year = year;
            
            statistics.add(statistic);
            
            if (month == 12 && j < yearsBack) {
                j++;
                month = 0;
            }
        }
        
        return statistics;
    }

    @Override
    public List<Statistic> getSalesNumber(int year) {
        int i = 0;
        
        List<Statistic> statistics = new ArrayList();
        while (i < 13) {
            int weekData = 0;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, i);
            Date start = cal.getTime();
            cal.set(Calendar.MONTH, (i + 1));
            Date end = cal.getTime();
            for (Order order : orders.values()) {
                if (!order.useForStatistic()) {
                    continue;
                }
                if (order.rowCreatedDate.after(start) && order.rowCreatedDate.before(end)) {
                    weekData += cartManager.calculateTotalCost(order.cart);
                }
            }
            i++;
            Statistic statistic = new Statistic();
            statistic.year = year;
            statistic.month = i;
            statistic.count = weekData;
            statistics.add(statistic);
        }        
        
        return statistics;
    }

    public void clear() {
        for (Order order : orders.values()) {
            deleteObject(order);
        }
        
        incrementingOrderId = 100000;
        orders.clear();
    }

    private double getPriceBasedOnUserDiscount(CartItem item, User user) {
        double price = productManager.getPrice(item.getProduct().id, item.getVariations());
        price = price * (user.discount / 100);
        return price;
    }
    
    private double getPriceBasedOnProgressiveModel(CartItem item) {
        double totalPrice = 0;
        Product product = item.getProduct();
        for (int i=1; i<=item.getCount(); i++) {
            boolean found = false;
            for (ProductDynamicPrice iprice : product.prices) {
                if (i >= iprice.from && i <= iprice.to ) {
                     totalPrice += getPriceForDynamicProductItem(item, iprice);
                     found = true;
                }
            }
            
            if (!found) {
                totalPrice += productManager.getPrice(product.id, item.getVariations());
            }
        }
        
        return totalPrice / (double)item.getCount();
    }
    
    private double getPriceBasedOnCount(CartItem item) {
        Product product = item.getProduct();
        for (ProductDynamicPrice iprice : product.prices) {
            int count = item.getCount();
            if (count >= iprice.from && count <= iprice.to ) {
                 return getPriceForDynamicProductItem(item, iprice);
            }
        }
        
        return productManager.getPrice(item.getProduct().id, item.getVariations());
    }

    private double getPriceForDynamicProductItem(CartItem item, ProductDynamicPrice price) {
        Product product = item.getProduct();
        if (product.dynamicPriceInPercent) {
            double retPrice = item.getProduct().getPrice(null);
            double discount = retPrice * price.price/100;
            return retPrice - discount;
        } else {
            return price.price;
        }
    }
    
    @Override
    public List<Order> getOrdersNotTransferredToAccountingSystem() {
        List<Order> allOrders = getOrders(new ArrayList(), null, null);
        List<Order> notTransferred = new ArrayList();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -24);
        Date twentyFourHoursAgo = cal.getTime();
        
        for (Order order : allOrders) {
            if (order.testOrder) {
                continue;
            }
            
            if (order.transferredToAccountingSystem) {
                continue;
            }
            
            // We do not transfer order until them are atleast 
            if (order.createdDate.after(twentyFourHoursAgo)) {
                System.out.println("Skipping : " + order.incrementOrderId + " date: " + order.createdDate);
                continue;
            }
            
            if (order.status == Order.Status.PAYMENT_COMPLETED && order.captured) {
                notTransferred.add(order);
            }
            
            if (order.payment != null && order.payment.paymentType.equals("ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment") && order.status == Order.Status.PAYMENT_COMPLETED) {
                notTransferred.add(order);
            }
        }
        
        return notTransferred;
    }

    @Override
    public List<Order> getAllOrdersOnProduct(String productId) throws ErrorException {
        List<Order> result = new ArrayList();
        for(Order order : orders.values()) {
            boolean found = false;
            for(CartItem item : order.cart.getItems()) {
                if(item != null && item.getProduct() != null) {
                    if(item.getProduct().id.equals(productId)) {
                        found = true;
                    }
                }
            }
            if(found) {
                result.add(order);
            }
        }
        return result;
    }

    @Override
    public Order getOrderSecure(String orderId) throws ErrorException {
        return orders.get(orderId);
    }

    @Override
    public List<Order> getOrdersToCapture() throws ErrorException {
        List<Order> ordersToReturn = new ArrayList();
        for(Order order :orders.values()) {
            if(order.status == Order.Status.PAYMENT_COMPLETED && !order.captured && !order.testOrder) {
                ordersToReturn.add(order);
            }
        }
        return ordersToReturn;
    }

    private void validatePaymentStatus(Order order) {
        Order inMemoryOrder = orders.get(order.id);
        
        if (inMemoryOrder == null) {
            return;
        }
        
        if (inMemoryOrder.status == Order.Status.PAYMENT_COMPLETED) {
            if (order.status == Order.Status.WAITING_FOR_PAYMENT) {
                throw new ErrorException(1034);
            }
            if (order.status == Order.Status.CREATED) {
                throw new ErrorException(1034);
            }
            if (order.status == Order.Status.PAYMENT_FAILED) {
                throw new ErrorException(1034);
            }
        }
    }
    
    @Override
    public void changeOrderType(String orderId, String paymentTypeId) {
        Order order = getOrder(orderId);
        if (order == null) {
            return;
        }
        
        Application app = storeApplicationPool.getApplication(paymentTypeId);
        
        if (app != null) {
            order.changePaymentType(app);
            saveObject(order);
        }
    }

  
    @Override
    public void updatePriceForOrderLine(String cartItemId, String orderId, double price) {
        Order order = getOrder(orderId);
        if (order == null) {
            return;
        }
        
        if (order.status == Order.Status.PAYMENT_COMPLETED) {
            return;
        }
        
        order.updatePrice(cartItemId, price);
        saveOrder(order);
    }

    @Override
    public HashMap<Long, SalesStats> getSalesStatistics(Long startDate, Long endDate, String type) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startDate*1000);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MILLISECOND, 10);
        cal.set(Calendar.MINUTE, 10);
        cal.set(Calendar.SECOND, 10);
        
        Statistics stats = new Statistics();
        LinkedHashMap<Long, SalesStats> result = new LinkedHashMap();
        
        while(true) {
            Integer year = cal.get(Calendar.YEAR);
            Integer day = cal.get(Calendar.DAY_OF_YEAR);
            SalesStats salestat = new SalesStats();
            for (Order order : orders.values()) {
                if (!order.useForStatistic()) {
                    continue;
                }

                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(order.createdDate);
                if (cal2.get(Calendar.YEAR) != year) {
                    continue;
                }
                if (cal2.get(Calendar.DAY_OF_YEAR) != day) {
                    continue;
                }

                if(type != null) {
                    if(order.payment != null && order.payment.paymentType != null && !order.payment.paymentType.equals(type)) {
                        continue;
                    }
                }

                salestat.totalAmount += cartManager.calculateTotalCost(order.cart);
                salestat.totalCount += cartManager.calculateTotalCount(order.cart);
                salestat.numberOfOrders++;
            }
            
            result.put(cal.getTimeInMillis()/1000, salestat);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTimeInMillis() > (endDate * 1000)) {
                break;
            }
        }
        return result;
    }

    @Override
    public List<Order> getOrdersFromPeriode(long start, long end, boolean statistics) throws ErrorException {
        List<Order> orderresult = new ArrayList();
        
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(start*1000);
        startDate.set(Calendar.HOUR_OF_DAY, 1);
        startDate.set(Calendar.MINUTE, 1);
        
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(startDate.getTimeInMillis());
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 59);
        
        for(Order order : orders.values()) {
            if(!order.useForStatistic() && statistics) {
                continue;
            }
            if(order.createdDate.after(startDate.getTime()) && order.createdDate.before(endDate.getTime())) {
                orderresult.add(order);
            }
        }
        
        return orderresult;
    }

}
