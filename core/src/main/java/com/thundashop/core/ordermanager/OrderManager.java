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
import com.thundashop.core.dibs.DibsManager;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.TreeNode;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.ordermanager.data.SalesStats;
import com.thundashop.core.ordermanager.data.Statistic;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
    private ListManager listManager;
    
    @Autowired
    private StoreApplicationInstancePool storeApplicationInstancePool;
    
    @Autowired
    private StoreApplicationPool storeApplicationPool;
    
    @Autowired
    private InvoiceManager invoiceManager;
    
    @Autowired
    private MessageManager messageManager;
    
    @Autowired
    private DibsManager dibsManager;
    
    @Autowired
    GrafanaManager grafanaManager;
    
    @Override
    public void addProductToOrder(String orderId, String productId, Integer count) throws ErrorException {
        Order order = getOrder(orderId);
        Product product = productManager.getProduct(productId).clone();
        order.cart.createCartItem(product, count);
        saveObject(order);
    }
    
    
    @Override
    public Order creditOrder(String orderId) {
        Order order = getOrderSecure(orderId);
        Order credited = order.jsonClone();
        for(CartItem item : credited.cart.getItems()) {
            item.setCount(item.getCount() * -1);
        }
        
        incrementingOrderId++;
        credited.incrementOrderId = incrementingOrderId;
        credited.isCreditNote = true;
        credited.status = Order.Status.CREATED;
        credited.parentOrder = order.id;
        credited.creditOrderId.clear();
        order.creditOrderId.add(credited.id);
        order.doFinalize();
        saveOrder(credited);
        saveOrder(order);
        return credited;
    }
    
    @Override
    public void updateCountForOrderLine(String cartItemId, String orderId, Integer count) {
        Order order = getOrder(orderId);
        if (order == null) {
            return;
        }
        if(count == 0) {
            order.cart.removeItem(cartItemId);
        } else {
            order.updateCount(cartItemId, count);
        }
        saveOrder(order);
    }
    
    
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataFromDatabase : data.data) {
            if (dataFromDatabase instanceof Order) {
                Order order = (Order) dataFromDatabase;
                if(order.deleted != null) {
                    if(order.incrementOrderId > 0) {
                        System.out.println("db[\"col_123865ea-3232-4b3b-9136-7df23cf896c6\"].update({\"_id\" : \""+order.id+"\"}, { $set: { deleted : null }})");
                    }
                }
                if (order.cart == null) {
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
        if(order.incrementOrderId > incrementingOrderId) {
            incrementingOrderId = order.incrementOrderId;
        }
        if(order.status == Order.Status.PAYMENT_COMPLETED && order.paymentDate == null) {
            order.paymentDate = new Date();
            String name = "";
            if(getSession() != null && getSession().currentUser != null) {
                name = getSession().currentUser.fullName;
            }
            if(order.payment != null && order.payment.transactionLog != null) {
                order.payment.transactionLog.put(System.currentTimeMillis(), "Order marked paid for by : " + name);
            }
        }
        saveObject(order);
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
                for (String variationKey : cartItem.getVariations().keySet()) {
                    String variationValue = cartItem.getVariations().get(variationKey);
                    TreeNode nodeKey = listManager.getJSTreeNode(variationKey);
                    TreeNode nodeValue = listManager.getJSTreeNode(variationValue);
                    newOrder += nodeKey + " " + nodeValue;
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
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if(new Double(product.price).isNaN()) {
                product.price = 0;
            }
        }
    }

    @Override
    public Order createOrder(Address address) throws ErrorException {
        Order order = createOrderInternally(address);
        setPreferredPayment(order);
        saveOrder(order);
        updateStockAndSendConfirmation(order);
        order.doFinalize();
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
        order.doFinalize();
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
        order.doFinalize();
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
        
        finalize(result);
        return result;
    }
    
    @Override
    public void checkForOrdersToCapture() throws ErrorException {
        dibsManager.checkForOrdersToCapture();
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
            finalizeOrder(order);
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
                order.doFinalize();
                return order;
            }
        }
        throw new ErrorException(61);
    }
    
    @Override
    public Double getTotalAmount(Order order) {
        if(order == null || order.cart == null) {
            return 0.0;
        }
        
        Double toPay = order.cart.getTotal(false);
        
        if (order.shipping != null && order.shipping.cost > 0) {
            toPay += order.shipping.cost;
        }
        
        if (order.payment != null && order.payment.paymentFee > 0) {
            toPay += order.payment.paymentFee;
        }
        if(toPay.isNaN()) {
            logPrint("Nan calc on order: " + order.incrementOrderId);
            return 0.0;
        }
        
        return toPay;
    }
    
    @Override
    public Double getTotalAmountExTaxes(Order order) {
        List<CartTax> taxes = getTaxes(order);
        double totalTax = 0.0;
        for(CartTax tax : taxes) {
            totalTax += tax.sum;
        }
        
        return getTotalAmount(order) - totalTax;
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
        order.doFinalize();
        feedGrafana(order);
        return order;
    }
    
    private void updateStockAndSendConfirmation(Order order) throws ErrorException {
        
        updateStockQuantity(order, "trackControl");
        updateCouponsCount(order);
        
        Application orderManagerApplication = storeApplicationPool.getApplication("27716a58-0749-4601-a1bc-051a43a16d14");
        if (!orderManagerApplication.getSetting("shouldSendEmail").equals("true")) {
            return;
        }
        
        sendMail(order);
    }
    
    private void sendMail(Order order) {
        Application orderManagerApplication = storeApplicationPool.getApplication("27716a58-0749-4601-a1bc-051a43a16d14");
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
        
        Map<String, String> files = new HashMap();
        
            
        Application invoiceApplicationIsActivated = storeApplicationPool.getApplication("70ace3f0-3981-11e3-aa6e-0800200c9a66");
        if (invoiceApplicationIsActivated != null) {
            String file = invoiceManager.getInvoiceFile(order.id);
            files.put(file, "Faktura "+order.incrementOrderId+".pdf");
        }
        
        
        if (!subject.isEmpty()) {
            mailFactory.sendWithAttachments(store.configuration.emailAdress, order.cart.address.emailAddress, subject, orderText, files, true);
            
            if (store.configuration.emailAdress != null && !store.configuration.emailAdress.equals(order.cart.address.emailAddress)) {
                mailFactory.sendWithAttachments(store.configuration.emailAdress, store.configuration.emailAdress, subject, orderText, files, true);
            }
        }
    }
    
    @Override
    public Order getOrderByReference(String referenceId) throws ErrorException {
        for (Order order : orders.values()) {
            if (order.reference.equals(referenceId)) {
                order.doFinalize();
                return order; 
            }
        }
        return null;
    }
    
    @Override
    public List<Order> getAllOrdersForUser(String userId) throws ErrorException {
        User user = getSession().currentUser;
        
        if(user == null) {
            return new ArrayList();
        }
        
        if(user.isCustomer()) {
            userId = user.id;
        }
        
        List<Order> returnOrders = new ArrayList();
        for (Order order : orders.values()) {
            if ((order.userId != null && order.userId.equals(userId))) {
                returnOrders.add(order);
            }
        }
        sortOrderList(returnOrders);
        finalize(returnOrders);
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
        
        List<Order> res = getOrders(listOrderIds, page, pageSize);
        finalize(res);
        return res;
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
                logPrint("Skipping : " + order.incrementOrderId + " date: " + order.createdDate);
                continue;
            }
            
            if (order.status == Order.Status.PAYMENT_COMPLETED && order.captured) {
                notTransferred.add(order);
            }
            
            if (order.payment != null && order.payment.paymentType.equals("ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment") && order.status == Order.Status.PAYMENT_COMPLETED) {
                notTransferred.add(order);
            }
        }
        finalize(notTransferred);
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
        Order order = orders.get(orderId);
        if(order != null) {
            order.doFinalize();
        }
        return order;
    }

    @Override
    public List<Order> getOrdersToCapture() throws ErrorException {
        List<Order> ordersToReturn = new ArrayList();
        for(Order order :orders.values()) {
            if(order.status == Order.Status.PAYMENT_COMPLETED && !order.captured && !order.testOrder) {
                ordersToReturn.add(order);
            }
        }
        finalize(ordersToReturn);
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
        finalize(orderresult);
        return orderresult;
    }

    @Override
    public Double getTotalForOrderById(String orderId) {
        Order order = getOrderSecure(orderId);
        return getTotalAmount(order);
    }

    public void orderPaid(String orderId) {
        Application orderManagerApplication = storeApplicationPool.getApplication("27716a58-0749-4601-a1bc-051a43a16d14");
        if (!orderManagerApplication.getSetting("shouldSendEmailAfterCompleted").equals("true")) {
            return;
        }
        
        Order order = getOrderSecure(orderId);
        
        if (order != null) {
            sendMail(order);
        }
    }

    @Override
    public void checkForOrdersToAutoPay() throws ErrorException {
        for(Order order : orders.values()) {
            if(!orderNeedAutoPay(order)) {
                continue;
            }
            logPrint("autopay for order: " + order.incrementOrderId);
            User user = userManager.getUserById(order.userId);
            for(UserCard card : user.savedCards) {
                if(card.isExpired()) {
                    continue;
                }
                
                try {
                    if(card.savedByVendor.equals("DIBS")) {
                        dibsManager.payWithCard(order, card);
                    }
                    if(order.status == Order.Status.PAYMENT_COMPLETED) {
                        messageManager.sendInvoiceForOrder(order.id);
                        break;
                    }
                    if(order.status == Order.Status.PAYMENT_FAILED) {
                        notifyAboutFailedPaymentOnOrder(order);
                    }
                }catch(Exception e) {
                    order.payment.transactionLog.put(System.currentTimeMillis(), "Fatal error when trying autopay.");
                    saveOrder(order);
                }
            }
        }
    }

    public void notifyAboutFailedPaymentOnOrder(Order order) {
        logPrint("Need to notify about failed payment");
    }

    private boolean orderNeedAutoPay(Order order) {
        if(order.cart.getTotal(true) <= 0) {
            return false;
        }
        if(order.status == Order.Status.PAYMENT_FAILED) {
            return false;
        }
        if(order.status >= Order.Status.PAYMENT_COMPLETED) {
            return false;
        }

        if(order.triedAutoPay()) {
            return false;
        }

        //Order has started, its too late.
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        for(CartItem item : order.cart.getItems()) {
            if(item.startDate != null && new Date().after(item.startDate)) {
                if(yesterday.getTime().after(order.rowCreatedDate)) {
                    return false;
                }
            }
        }
        
        User user = userManager.getUserById(order.userId);
        if(user == null) {
            return false;
        }

        if(user.savedCards.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public FilteredData getOrdersFiltered(FilterOptions filterOptions) {
        List<Order> allOrders = orders.values().stream()
                .filter(filterOrdersByDate(filterOptions))
                .filter(filterOrdersBySearchWord(filterOptions))
                .filter(filterOrdersByStatus(filterOptions))
                .collect(Collectors.toList());
        
        if(filterOptions.extra.containsKey("paymenttype")) {
            String type = filterOptions.extra.get("paymenttype");
            List<Order> newOrderList = new ArrayList();
            if(type != null) {
                type = type.replace("-", "_");
                for(Order order : allOrders) {
                    if(order.payment != null && order.payment.paymentType != null) {
                        if(order.payment.paymentType.contains(type)) {
                            newOrderList.add(order);
                        }
                    }
                }
            }
            allOrders = newOrderList;
        }
        
        sortOrderList(allOrders);
        finalize(allOrders);
        return pageIt(allOrders, filterOptions);
    }

    private Predicate<? super Order> filterOrdersByDate(FilterOptions filterOptions) {
        if (filterOptions.startDate == null || filterOptions.endDate == null) {
            return order -> order != null;
        }
        
        return order -> order.createdBetween(filterOptions.startDate, filterOptions.endDate);
    }


    private Predicate<? super Order> filterOrdersBySearchWord(FilterOptions filterOptions) {
        return order -> order.matchOnString(filterOptions.searchWord);
    }

    private Predicate<? super Order> filterOrdersByStatus(FilterOptions filterOptions) {
        if (!filterOptions.extra.containsKey("orderstatus")) {
            return o -> o != null;
        }
        
        if (filterOptions.extra.get("orderstatus").equals("0")) {
            return o -> o != null;
        }
        
        return order -> new Integer(order.status).equals(Integer.valueOf(filterOptions.extra.get("orderstatus")));
    }

    private void sortOrderList(List<Order> returnOrders) {

        Collections.sort(returnOrders ,new Comparator<Order>(){
        public int compare(Order o1, Order o2){
            if(o1.incrementOrderId == o2.incrementOrderId)
                return 0;
            return o1.incrementOrderId > o2.incrementOrderId ? -1 : 1;
        }
        });
    }

    private void finalize(List<Order> result) {
        for(Order order : result) {
            order.doFinalize();
        }
        
    }

    @Override
    public boolean payOrderByCard(String cardId, String orderId) throws Exception {
        Order order = getOrder(orderId);
        User user = userManager.getUserById(order.userId);
        if(user != null) {
            for(UserCard card : user.savedCards) {
                if(card.card.equals(cardId)) {
                    if(card.savedByVendor.equalsIgnoreCase("dibs")) {
                        dibsManager.payWithCard(order, card);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void forceDeleteOrder(Order order) {
        deleteObject(order);
        orders.remove(order.id);
    }

    private void setPreferredPayment(Order order) {
        User user = userManager.getLoggedOnUser();
        
        Payment payMent = null;
        
        if (user != null ) {
            payMent =  getUserPrefferedPaymentMethod(user.id);
        }
        
        if (payMent == null) {
            payMent = getStorePreferredPayementMethod();
        }
        
        if (payMent != null) {
            order.payment = payMent;
        }
    }

    public Payment getUserPrefferedPaymentMethod(String userId) {
        User user = userManager.getUserById(userId);
        
        if (user == null)
            return null;
        
        if (user.preferredPaymentType == null || user.preferredPaymentType.isEmpty()) {
            return null;
        }
        
        Application paymentApplication = applicationPool.getApplication(user.preferredPaymentType);
        if (paymentApplication != null) { 
            Payment payment = new Payment();
            payment.paymentType = "ns_" + paymentApplication.id.replace("-", "_") + "\\" + paymentApplication.appName;
            return payment;
        }
        
        
        return null;
    }

    public Payment getStorePreferredPayementMethod() {
        Application ecommerceSettingsApplication = applicationPool.getApplication("9de54ce1-f7a0-4729-b128-b062dc70dcce");
        String defaultPaymentApplicationId = ecommerceSettingsApplication.getSetting("defaultPaymentMethod");
        
        if(defaultPaymentApplicationId != null && !defaultPaymentApplicationId.isEmpty()) {
            Application paymentApplication = applicationPool.getApplication(defaultPaymentApplicationId);
            if (paymentApplication != null) { 
                Payment payment = new Payment();
                payment.paymentType = "ns_" + paymentApplication.id.replace("-", "_") + "\\" + paymentApplication.appName;
                return payment;
            }
        }
        
        return null;
    }

    @Override
    public void sendReciept(String orderId, String email) {
        messageManager.sendInvoiceForOrder(orderId, email);
    }

    @Override
    public void markAsInvoicePayment(String orderId) {
        Order order = getOrderSecure(orderId);
        order.payment.paymentType = "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment";
        saveOrder(order);
    }

    private void finalizeOrder(Order order) {
        List<Order> ordersToFinalise = new ArrayList();
        ordersToFinalise.add(order);
        finalize(ordersToFinalise);
    }

    private void feedGrafana(Order order) {
        HashMap<String, Object> toAdd = new HashMap();
        
        toAdd.put("inktax", getTotalAmount(order));
        toAdd.put("extax", getTotalAmountExTaxes(order));
        toAdd.put("items", (Number)order.cart.getItems().size());
        if(order.payment != null) {
            toAdd.put("payment", order.payment.paymentType);
        }
        toAdd.put("storeid", (String)storeId);
        
        GrafanaFeeder feeder = new GrafanaFeeder();
        grafanaManager.addPoint("webdata", "ordercreated", toAdd);
    }

    @Override
    public void setExternalRefOnCartItem(String cartItem, String externalId) {
        for(Order order : orders.values()) {
            for(CartItem item : order.cart.getItems()) {
                if(item.getCartItemId().equals(cartItem)) {
                    item.getProduct().externalReferenceId = externalId;
                    saveOrder(order);
                    break;
                }
            }
        }
    }

}
