package com.thundashop.core.ordermanager;

import com.google.gson.Gson;
import com.thundashop.core.appmanager.AppManager;
import com.thundashop.core.appmanager.data.ApplicationSubscription;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.dibs.DibsManager;
import com.thundashop.core.hotelbookingmanager.BookingReference;
import com.thundashop.core.hotelbookingmanager.HotelBookingManager;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
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
                
                if(order.payment.paymentType.isEmpty()) {
                    order.payment.paymentType = order.payment.paymentType = "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment";
                    order.status = Order.Status.COMPLETED;
                }
                
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

    
    /**
     * 
     * @param orderId
     * @return True weather or not the order where captured, just that it where able to complete the order.
     */
    @Override
    public void captureOrder(String orderId) throws Exception, ErrorException {
        Order order = getOrder(orderId);
        if(order == null) {
            throw new Exception("Error: not found order");
        }
        order.payment.transactionLog.put(System.currentTimeMillis(), "Capturing order");
        if(order.payment.paymentType.toLowerCase().contains("dibs")) {
            captureWithDibs(order);
        }
        
    }

    
    private void saveOrderInternal(Order order) throws ErrorException {
        User user = getSession().currentUser;
        if (user != null && order.userId == null) {
            order.userId = user.id;
        }
        
        if (order.session == null) {
            order.session = getSession().id;
        }

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
        text = text.replace("/displayImage", "http://" + getStore().webAddress + "/displayImage");
        text = text.replace("{Order.Id}", order.id);
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
        if (order.cart.address.countryname != null && !order.cart.address.countryname.isEmpty()) {
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
            if (value != null) {
                return value;
            }
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
        UserManager usermgr = getManager(UserManager.class);
        User user = usermgr.getUserByReference(referenceKey);
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

        if(orderIds != null && !orderIds.isEmpty()) {
            List<Order> toReturn = new ArrayList();
            for(Order ord : result) {
                if(orderIds.contains(ord.id)) {
                    toReturn.add(ord);
                }
            }
            result = toReturn;
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
    public void updateOrderStatusInsecure(String orderId, int status) throws ErrorException {
        if (status == Order.Status.COMPLETED) {
            return;
        }
        changeOrderStatus(orderId, status);
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

    public void setOrdersActivatedByReferenceId(BookingReference reference) throws ErrorException {
        boolean expirationDateSet = false;
        for (Order order : orders.values()) {
            
            if (order.reference.equals(""+reference.bookingReference)) {
                order.activated = true;
                order.expiryDate = null;
                
                if (!expirationDateSet) {
                    expirationDateSet = true;
                    order.expiryDate = getExpirationDate(order, reference);
                }
                
                saveOrder(order);
            }
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
            if (!order.id.equals(orderId)) {
                continue;
            }
            String sessionId = getSession().id;
            if (user == null) {
                if (order.session != null && order.session.equals(sessionId)) {
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
        CartManager cartManager = getManager(CartManager.class);
        cartManager.updateCoupons(order.cart.coupon);
    }

    @Override
    public List<CartTax> getTaxes(Order order) throws ErrorException {
        return order.cart.getCartTaxes();
    }

    private Order createOrderInternally(Address address) throws ErrorException {
        CartManager cartManager = getManager(CartManager.class);
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

        setIncrementalOrderId(order);
        return order;
    }

    private void updateStockAndSendConfirmation(Order order) throws ErrorException {

        updateStockQuantity(order, "trackControl");
        updateCouponsCount(order);

        Store store = this.getStore();
        String orderText = getCustomerOrderText(order);

        String subject = getSubject();
        HashMap<String, Setting> settings = getSettings("Settings");

        if (settings != null && settings.containsKey("stoporderemail") && settings.get("stoporderemail").value.equals("true")) {
            return;
        }

        if (!subject.isEmpty()) {
            mailFactory.send(store.configuration.emailAdress, order.cart.address.emailAddress, getSubject(), orderText);

            if (store.configuration.emailAdress != null && !store.configuration.emailAdress.equals(order.cart.address.emailAddress)) {
                mailFactory.send(store.configuration.emailAdress, store.configuration.emailAdress, getSubject(), orderText);
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
    public List<Order> getAllOrderByReference(String referenceId) throws ErrorException {
        List<Order> result = new ArrayList();
        for (Order order : orders.values()) {
            if (order.reference.equals(referenceId)) {
                result.add(order);
            }
        }
        
        
        Collections.sort(result, new Comparator<Order>(){
            public int compare(Order o1, Order o2){
                if(o1.incrementOrderId == o2.incrementOrderId)
                    return 0;
                return o1.incrementOrderId < o2.incrementOrderId ? -1 : 1;
            }
       });
         
       return result;
    }

    @Override
    public List<Order> getAllOrdersForUser(String userId) throws ErrorException {
        User user = getSession().currentUser;
        List<Order> returnOrders = new ArrayList();
        for (Order order : orders.values()) {
            if (order.userId != null && order.userId.equals(userId)) {
                if ((user != null && order.userId.equals(user.id)) || user.isAdministrator()) {
                    returnOrders.add(order);
                }
            }
        }
        return returnOrders;
    }

    @Override
    public void logTransactionEntry(String orderId, String entry) throws ErrorException {
        Order order = getOrder(orderId);
        order.payment.transactionLog.put(new Date().getTime(), entry);
        saveOrder(order);
    }

    public void setTriedToSendOrderToAccountingSystem(Order order) throws ErrorException {
        Order inMemOrder = orders.get(order.id);
        if (inMemOrder != null) {
            inMemOrder.triedTransferredToAccountingSystem = true;
            saveObject(inMemOrder);
        }
    }

    public void setDontSendToAccountSystem(Order order) throws ErrorException {
        Order inMemOrder = orders.get(order.id);
        if (inMemOrder != null) {
            inMemOrder.transferedToAccountingSystem = true;
            saveObject(inMemOrder);
        }
    }

    @Override
    public void setAllOrdersAsTransferedToAccountSystem() throws ErrorException {
        HotelBookingManager manager = getManager(HotelBookingManager.class);
        
        String string = "November 25, 2014";
        Date date = null;
        try {
             date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(string);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(OrderManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (date == null) {
            return;
        }
        
        Set<String> referencesToFix = new HashSet<String>();
        
        for (Order order : orders.values()) {
            if (order.rowCreatedDate.after(date)) {
                referencesToFix.add(order.reference);
                System.out.println("Skipping: " + order.id);
                continue; 
            }
            order.transferedToAccountingSystem = true;
            saveObject(order);
        }
        
        
        for (String reference : referencesToFix) {
            BookingReference ref = manager.getReservationByReferenceId(Integer.valueOf(reference));
            int i = 0;
            for (Order order : orders.values()) {
                if (order.reference.equals(reference)) {
                    i++;
                    setDate(order, ref.startDate, i);
                }
            }
        }
        
        for (BookingReference ref: manager.getAllReservations()) {
            if (referencesToFix.contains(""+ref.bookingReference)) {
                continue;
            }
            manager.confirmReservation(ref.bookingReference);
        }
        
        checkForRecurringPayments();
    }
    
    private void setDate(Order order, Date startDate, int i) throws ErrorException {
        i--;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, i);
        order.startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        order.endDate = calendar.getTime();
        order.expiryDate = null;
        
        System.out.println("i: " + i + " " + order.startDate);
        saveObject(order);
    }

    @Override
    public void checkForRecurringPayments() throws ErrorException {
        Date today = new Date();

        // Need to make a second list to avoid concurrent modifications problems.
        List<String> ordersToRenew = new ArrayList();
        for (Order order : orders.values()) {
            if (order.expiryDate != null) {
                if (today.after(order.expiryDate)) {
                    ordersToRenew.add(order.id);
                }
            }
        }

        for (String orderId : ordersToRenew) {
            Order order = orders.get(orderId);
            renewOrder(order);
        }
    }

    @Override
    public void setExpiryDate(String orderId, Date date) throws ErrorException {
        Order order = orders.get(orderId);
        if (order != null) {
            order.expiryDate = date;
            saveObject(order);
        }
    }

    public void unsetExpiryDateByReference(String referenceId) throws ErrorException {
        for (Order order : orders.values()) {
            if (order.reference != null && order.reference.equals(referenceId)) {
                order.expiryDate = null;
                saveObject(order);
            }
        }
    }

    private void renewOrder(Order order) throws ErrorException {
        Order copiedOrder = order.jsonClone();
        Calendar cal = Calendar.getInstance();
        cal.setTime(order.expiryDate);

        if (copiedOrder.recurringMonths != null) {
            cal.add(Calendar.MONTH, copiedOrder.recurringMonths);
            copiedOrder.expiryDate = cal.getTime();
        } else if (copiedOrder.recurringDays != null) {
            cal.add(Calendar.DATE, copiedOrder.recurringDays);
            copiedOrder.expiryDate = cal.getTime();
        }

        copiedOrder.createdDate = order.expiryDate;
        copiedOrder.rowCreatedDate = order.expiryDate;
        if (copiedOrder.cart != null) {
            copiedOrder.cart.rowCreatedDate = order.expiryDate;
        }
        
        // This id done to set count to 1 for SemLagerhotell HardCoded to save time!!
        for (CartItem item : copiedOrder.cart.getItems()) {
            item.setCount(1);
        }

        cal.add(Calendar.MONTH, -1);
        cal.add(Calendar.DATE, 15);
        copiedOrder.startDate = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        copiedOrder.endDate = cal.getTime();
        
        setIncrementalOrderId(copiedOrder);
        saveOrder(copiedOrder);

        order.expiryDate = null;
        saveOrder(order);
    }

    private void setIncrementalOrderId(Order order) {
        incrementingOrderId++;
        order.incrementOrderId = incrementingOrderId;
    }

    private Date getExpirationDate(Order order, BookingReference reference) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(reference.startDate);
         calendar.add(Calendar.MONTH, 3);
         calendar.add(Calendar.DAY_OF_MONTH, -15);
         
         return calendar.getTime();
    }

    private void captureWithDibs(Order order) throws Exception, ErrorException {
        DibsManager dibsManager = getManager(DibsManager.class);
        CartManager cartManager = getManager(CartManager.class);
        UserManager userManager = getManager(UserManager.class);
        MessageManager messageManager = getManager(MessageManager.class);
        StoreManager storeManager = getManager(StoreManager.class);
        
        Double amount = cartManager.calculateTotalCost(order.cart);
        int toCapture = new Double(amount*100).intValue();
        dibsManager.captureOrder(order, toCapture);
        saveOrder(order);
        if(order.captured) {
            User user = userManager.getUserById(order.userId);
            if(user != null) {
                HashMap<String, String> attachments = new HashMap();
                attachInvioce(attachments, order.id);
                String title = "Receipt for payment";
                String message = "Attached you will find your reciept for the payment for order id: " + order.incrementOrderId + ", amount: " + order.cart.getTotal(false);
                String name = user.fullName;
                String email = user.emailAddress;
                String copyadress = storeManager.getMyStore().configuration.emailAdress;    
                
                messageManager.sendMailWithAttachments(email, name, title, message, copyadress, copyadress, attachments);
            }
        }
    }
    
    private void attachInvioce(HashMap<String, String> attachments, String orderId) throws ErrorException {
        InvoiceManager invoiceManager  = getManager(InvoiceManager.class);
        String invoice = invoiceManager.getBase64EncodedInvoice(orderId);
        if (invoice != null && !invoice.isEmpty()) {
            attachments.put("reciept.pdf", invoice);
        }
    }
}
