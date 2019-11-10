package com.thundashop.core.ordermanager;

import com.getshop.pullserver.PullMessage;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.thundashop.core.applications.GetShopApplicationPool;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bambora.BamboraManager;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.department.Department;
import com.thundashop.core.department.DepartmentManager;
import com.thundashop.core.dibs.DibsManager;
import com.thundashop.core.epay.EpayManager;
import com.thundashop.core.getshop.GetShopPullService;
import com.thundashop.core.getshopaccounting.AccountingBalance;
import com.thundashop.core.getshopaccounting.DayEntry;
import com.thundashop.core.getshopaccounting.DayIncome;
import com.thundashop.core.getshopaccounting.DayIncomeFilter;
import com.thundashop.core.getshopaccounting.DayIncomeReport;
import com.thundashop.core.getshopaccounting.DayIncomeTransferToAaccountingInformation;
import com.thundashop.core.getshopaccounting.DiffReport;
import com.thundashop.core.getshopaccounting.DoublePostAccountingTransfer;
import com.thundashop.core.getshopaccounting.OrderDailyBreaker;
import com.thundashop.core.getshopaccounting.OrderUnsettledAmountForAccount;
import com.thundashop.core.giftcard.GiftCardManager;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.gsd.GdsPaymentAction;
import com.thundashop.core.gsd.GetShopDevice;
import com.thundashop.core.gsd.TerminalResponse;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.TreeNode;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ocr.StoreOcrManager;
import com.thundashop.core.ordermanager.data.AccountingFreePost;
import com.thundashop.core.ordermanager.data.CartItemDates;
import com.thundashop.core.ordermanager.data.ClosedOrderPeriode;
import com.thundashop.core.ordermanager.data.EhfSentLog;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderManagerSettings;
import com.thundashop.core.ordermanager.data.OrderFilter;
import com.thundashop.core.ordermanager.data.OrderLight;
import com.thundashop.core.ordermanager.data.OrderResult;
import com.thundashop.core.ordermanager.data.OrderShipmentLogEntry;
import com.thundashop.core.ordermanager.data.OrderTransaction;
import com.thundashop.core.ordermanager.data.OrderTransactionDTO;
import com.thundashop.core.ordermanager.data.OrdersToAutoSend;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.ordermanager.data.PaymentTerminalInformation;
import com.thundashop.core.ordermanager.data.PmiResult;
import com.thundashop.core.ordermanager.data.SalesStats;
import com.thundashop.core.ordermanager.data.Statistic;
import com.thundashop.core.ordermanager.data.VirtualOrder;
import com.thundashop.core.paymentmanager.PaymentManager;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pos.PosConference;
import com.thundashop.core.pos.PosManager;
import com.thundashop.core.printmanager.ReceiptGenerator;
import com.thundashop.core.printmanager.PrintJob;
import com.thundashop.core.printmanager.PrintManager;
import com.thundashop.core.printmanager.Printer;
import com.thundashop.core.printmanager.StorePrintManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.AccountingDetail;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.stripe.StripeManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import com.thundashop.core.verifonemanager.VerifoneFeedback;
import com.thundashop.core.warehousemanager.WareHouseManager;
import com.thundashop.core.webmanager.WebManager;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@GetShopSession
public class OrderManager extends ManagerBase implements IOrderManager {

    private long incrementingOrderId = 100000;
    
    public HashMap<String, Order> orders = new HashMap(); 
   
    public HashMap<String, VirtualOrder> virtualOrders = new HashMap();
    
    public HashMap<String, ClosedOrderPeriode> closedPeriodes = new HashMap();
    
    public HashMap<String, AccountingFreePost> accountingFreePosts = new HashMap();
    
    private Set<String> ordersChanged = new TreeSet();
    
    private Set<String> ordersCreated = new TreeSet();
    
    public OrdersToAutoSend ordersToAutoSend = new OrdersToAutoSend();
    
    private OrderManagerSettings orderManagerSettings = null;
    
    @Autowired
    public MailFactory mailFactory;
    
    @Autowired
    public StoreManager StoreManager;
    
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
    private BamboraManager bamboraManager;
    
    @Autowired
    private EpayManager epayManager;
    
    @Autowired
    private StripeManager stripeManager;
    
    @Autowired
    private GrafanaManager grafanaManager;
    
    @Autowired
    private StorePrintManager storePrintManager;
    
    @Autowired
    private PrintManager printManager;
    
    @Autowired
    private GetShopApplicationPool getShopApplicationPool; 
    
    @Autowired
    private GiftCardManager giftCardManager;
    
    @Autowired
    private GetShopPullService getShopPullService; 
    private boolean queuedEmptied = false;
    
    @Autowired
    private PaymentManager paymentManager;
    
    @Autowired
    private DepartmentManager departmentManager;

    @Autowired
    private GetShopSessionScope getShopSpringScope; 
    
    @Autowired
    private GdsManager gdsManager;
    
    @Autowired
    private WebManager webManager;
    
    @Autowired
    private PosManager posManager;
    
    @Autowired
    private WareHouseManager wareHouseManager;
    
    @Autowired
    private StoreOcrManager storeOcrManager;
    
    private List<String> terminalMessages = new ArrayList();
    private Order orderToPay;
    private String tokenInUse;
    
    @Override
    public void addProductToOrder(String orderId, String productId, Integer count) throws ErrorException {
        Order order = getOrder(orderId);
        Product product = productManager.getProduct(productId).clone();
        order.cart.createCartItem(product, count);
        saveObject(order);
    }
    

    @Override
    public String createRegisterCardOrder(String paymentTypeId) {
        User user = userManager.getUserById(getSession().currentUser.id);
        user.preferredPaymentType = paymentTypeId;
        userManager.saveUserSecure(user);
        
        cartManager.clear();
        Order order = createOrderForUser(getSession().currentUser.id);
        return order.id;
    }    
    
    
    @Override
    public String getCurrentPaymentOrderId() {
        if (orderToPay != null) {
            return orderToPay.id;
        }
        
        return null;
    }
    
    @Override
    public Order creditOrder(String orderId) {
        Order credited = createCreatditOrder(orderId, "");
        return credited;
    }

    private Order createCreatditOrder(String orderId, String newReference) throws ErrorException {
        Order order = getOrderSecure(orderId);

        if (order.createdBasedOnOrderIds != null && !order.createdBasedOnOrderIds.isEmpty()) {
            addCreditNotesToBookings(order.createdBasedOnOrderIds);
        }
        
        Order credited = order.jsonClone();
        for(CartItem item : credited.cart.getItems()) {
            item.setCount(item.getCount() * -1);
            item.creditPmsAddonsAndPriceMatrix();
        }
        
        credited.incrementOrderId = getNextIncrementalOrderId();
        credited.isCreditNote = true;
        credited.status = Order.Status.CREATED;
        credited.parentOrder = order.id;
        credited.creditOrderId.clear();
        credited.orderTransactions.clear();
        credited.closed = false;
        credited.transferredToAccountingSystem = false;
        credited.moveAllTransactionToTodayIfItsBeforeDate(getOrderManagerSettings().closedTilPeriode);
        if (credited.overrideAccountingDate != null && credited.overrideAccountingDate.before(getOrderManagerSettings().closedTilPeriode)) {
            credited.overrideAccountingDate = getOrderManagerSettings().closedTilPeriode;
        }
        
        order.creditOrderId.add(credited.id);
        order.doFinalize();
        
        if (!newReference.isEmpty() && order.cart != null) {
            credited.cart.reference = newReference;
        }

        saveOrder(credited);
        saveOrder(order);
        
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            markAsPaidWithTransactionTypeInternal(order.id, order.getTotalAmount(), new Date(), 1, "unkown", order.getTotalAmountLocalCurrency(), order.getTotalRegisteredAgio());
            markAsPaidWithTransactionTypeInternal(credited.id, credited.getTotalAmount(), new Date(), 1, "unkown", credited.getTotalAmountLocalCurrency(), credited.getTotalRegisteredAgio());   
        }
        
        revertOrderLinesToPreviouseState(order);
        
        List<String> credittedOrders = new ArrayList();
        credittedOrders.add(credited.id);
        try {
            addOrdersToBookings(credittedOrders);
        }catch(GetShopBeanException ex) {
            // This will happen if the credit order is invoked within a named bean, 
            // normally it then comes from the pms manager itself and then the manager
            // should handle the adding to booking properly itself.
            return credited;
        }
        
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
            if (dataFromDatabase instanceof VirtualOrder) {
                virtualOrders.put(dataFromDatabase.id, (VirtualOrder)dataFromDatabase);
            }
            
            if (dataFromDatabase instanceof AccountingFreePost) {
                AccountingFreePost freePost = (AccountingFreePost)dataFromDatabase;
                accountingFreePosts.put(freePost.id, freePost);
            }

            
            if (dataFromDatabase instanceof Order) {
                Order order = (Order) dataFromDatabase;
//                if (order.cleanMe()) {
//                    saveObject(order);
//                }
                
                if(order.payment != null && order.payment.paymentType != null && order.payment.paymentType.equals("ns_d02f8b7a_7395_455d_b754_888d7d701db8//Dibs")) {
                    order.payment.paymentType = "ns_d02f8b7a_7395_455d_b754_888d7d701db8\\Dibs";
                    saveObject(order);
                }

                if (order.cart == null) {
                    continue;
                }
                if (order.incrementOrderId > incrementingOrderId) {
                    incrementingOrderId = order.incrementOrderId;
                }
                if (order.isVirtual) {
                    continue;
                }
                order.isMatrixAndItemsValid();
                orders.put(order.id, order);
            }
            
        }
        
        // This function can be removed upon any release after 16 aug 2019
        cleanupEmptyAddonIds();
        
        createScheduler("ordercapturecheckprocessor", "2,7,12,17,22,27,32,37,42,47,52,57 * * * *", CheckOrdersNotCaptured.class);
        if(storeId.equals("c444ff66-8df2-4cbb-8bbe-dc1587ea00b7")) {
            checkChargeAfterDate();
        }
    }

    @Override
    public void initialize() throws SecurityException {
        super.initialize(); //To change body of generated methods, choose Tools | Templates.
        
        if (storeId.equals("13442b34-31e5-424c-bb23-a396b7aeb8ca")) {
            createScheduler("ordercollector", "*/10 * * * *", CheckOrderCollector.class);
        } else {
            stopScheduler("ordercollector");
        }
    }
    
    public void saveOrderInternal(Order order) throws ErrorException {
        if (order.isVirtual) {
            return;
        }
        
        order.clearPeriodisation();
        
        User user = getSession().currentUser;
        if (user != null && order.userId == null) {
            order.userId = user.id;
        }
        order.session = getSession().id;
        
        order.storeId = storeId;
        if(order.incrementOrderId > incrementingOrderId) {
            incrementingOrderId = order.incrementOrderId;
        }
        
        if(order.status == Order.Status.NEEDCOLLECTING && order.needCollectingDate == null) {
            order.needCollectingDate = new Date();
        }
        
        if(order.status == Order.Status.PAYMENT_COMPLETED && order.paymentDate == null) {
            markAsPaidInternal(order, new Date(), 0.0);
        }
        saveObject(order);
        boolean newOrder = !orders.containsKey(order.id);
        orders.put(order.id, order);
        
        if (newOrder) {
            ordersCreated.add(order.id);
        } else {
            ordersChanged.add(order.id);   
        }
    }

    @Override
    public void markAsPaid(String orderId, Date date, Double amount) {
        markAsPaidWithTransactionType(orderId, date, amount, 1, "unkown");
    }
    
    public void markAsPaidInternal(Order order, Date date, Double amount) {
        checkPaymentDateValidation(order, date);
        
        giftCardManager.createNewCards(order);
        
        if (order.isGiftCard()) {
            giftCardManager.registerOrderAgainstGiftCard(order, amount);
        }
        
        order.paymentDate = date;
        order.markedPaidDate = new Date();
        order.status = Order.Status.PAYMENT_COMPLETED;
        order.captured = true;
        
        String name = "";
        if(getSession() != null && getSession().currentUser != null) {
            name = getSession().currentUser.fullName;
            order.markedAsPaidByUserId = getSession().currentUser.id;
        }
        if(order.payment != null && order.payment.transactionLog != null) {
            order.payment.transactionLog.put(System.currentTimeMillis(), "Order marked paid for by : " + name);
        }
        
        saveObject(order);
    }
    
    private void unMarkPaidOrder(Order order) {
        order.paymentDate = null;
        order.status = Order.Status.WAITING_FOR_PAYMENT;
        order.markedAsPaidByUserId = "";
        order.payment.transactionLog.put(System.currentTimeMillis(), "Order unmarked as paid : " + getSession().currentUser.fullName);
        order.creditOrderId = new ArrayList();
        saveObject(order);
    }

    private void checkPaymentDateValidation(Order order, Date date) {
        try {
            Order cloned = (Order) order.clone();
            cloned.paymentDate = date;
            cloned.markedPaidDate = new Date();
            cloned.status = Order.Status.PAYMENT_COMPLETED;
            validateOrder(cloned);
        } catch (CloneNotSupportedException ex) {
            logPrintException(ex);
        }
    }
    
    private HashMap<String, Setting> getSettings(String phpApplicationName) throws ErrorException {
        return storeApplicationInstancePool.getApplicationInstanceSettingsByPhpName(phpApplicationName);
    }
    
    @Override
    public void cancelPaymentProcess(String tokenId) {
        printFeedBack("payment failed");
        orderToPay = null;
        
        GdsPaymentAction paymentAction = new GdsPaymentAction();
        paymentAction.action = GdsPaymentAction.Actions.CANCELPAYMENT;
        
        GetShopDevice device = gdsManager.getDeviceByToken(tokenId);
        gdsManager.sendMessageToDevice(device.id, paymentAction);
    }
    
    @Override
    public void logTransactionEntry(String orderId, String entry) throws ErrorException {
        Order order = getOrder(orderId);
        if(order == null) {
            try {
                Integer incOrderId = new Integer(orderId);
                order = getOrderByincrementOrderId(incOrderId);
            }catch(Exception e) {
                //No need to try to continue from here.
                return;
            }
        }
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

    public Order createOrderDummy(Address address) throws ErrorException {
        Order order = createOrderInternally(address, true);
        setPreferredPayment(order);
        return order;
    }
    
    @Override
    public Order createOrder(Address address) throws ErrorException {
        Order order = createOrderInternally(address, false);
        setPreferredPayment(order);
        saveOrder(order);
        order.doFinalize();
        return order;
    }
    
    public VirtualOrder createVirtualOrder(Address address, String virtualOrderReference) {
        VirtualOrder virtualOrder = new VirtualOrder();
        virtualOrder.order = createOrderDummy(address);
        virtualOrder.order.id = UUID.randomUUID().toString();
        virtualOrder.order.rowCreatedDate = new Date();
        virtualOrder.order.status = Order.Status.PAYMENT_COMPLETED;
        virtualOrder.order.isVirtual = true;
        virtualOrder.reference = virtualOrderReference;
        saveObject(virtualOrder);
        
        virtualOrders.put(virtualOrder.id, virtualOrder);
        
        return virtualOrder;
    }
    
    public void deleteAllVirtualOrders() {
        for(VirtualOrder vorder : virtualOrders.values()) {
            deleteVirtualOrders(vorder.reference);
        }
    }
    
    public void deleteVirtualOrders(String virtualOrderReference) {
        List<VirtualOrder> virtOrders = virtualOrders.values()
                .stream()
                .filter(virt -> virt.reference.equals(virtualOrderReference))
                .collect(Collectors.toList());
        
        for (VirtualOrder virt : virtOrders) {
            virtualOrders.remove(virt.id);
        }
    }
    
    public boolean isThereVirtualOrders(String virtualOrderReference) {
        return virtualOrders.values()
                .stream()
                .anyMatch(virt -> virt.reference.equals(virtualOrderReference));
    }
    
    public List<Order> getAllOrderIncludedVirtual() {
        List<Order> retOrders = getAllOrders();
        List<Order> retVirtualOrders = this.virtualOrders.values().stream().map(virt -> virt.order).collect(Collectors.toList());
        
        List<Order> all = new ArrayList();
        all.addAll(retOrders);
        all.addAll(retVirtualOrders);
        
        finalize(all);
        
        return all;
    }
    
    @Override
    public Order createOrderForUser(String userId) {
        User user = userManager.getUserById(userId);
        if (user == null) {
            throw new ErrorException(26);
        }
            
        Address address = user.address;
        Order order = createOrderInternally(address, false);
        order.userId = user.id;
        order.payment = getUserPrefferedPaymentMethod(userId);
        saveOrder(order);
        order.doFinalize();
        return order;
    }
    
    @Override
    public Order createOrderByCustomerReference(String referenceKey) throws ErrorException {
        User user = userManager.getUserByReference(referenceKey);
        user.address.phone = user.cellPhone;
        user.address.fullName = user.fullName;
        Order order = createOrderInternally(user.address, false);
        order.userId = user.id;
        saveOrder(order);
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
    public void startCheckForOrdersToCapture(String internalPassword) throws ErrorException {
        CheckOrderCollector check = new CheckOrderCollector();
        createProcessor(check);
    }
    
    @Override
    public void checkForOrdersToCapture(String internalPassword) throws ErrorException {
        if (internalPassword == null || !internalPassword.equals("asfasdfuj2843ljsdflansfkjn432k5lqjnwlfkjnsdfklajhsdf2")) {
            return;
        }
        
        logPrint("Checking for orders to collect.. " + getSession().currentUser.fullName);
        boolean dibs = dibsManager.checkForOrdersToCapture();
        boolean epay = epayManager.checkForOrdersToCapture();
//        bamboraManager.checkForOrdersToCapture();

        if (dibs || epay) {
            emptyPullServerQueue();
        }
    }
    
    @Override
    public void saveOrder(Order order) throws ErrorException {
        
        if(order.isPaid() && !order.isNotified() && order.isPaymentLinkType()) {
            order.markAsAutosent();
            markOrderForAutoSending(order.id);
        }
        
        validateOrder(order);
        saveOrderInternal(order);
        
        try {
            updateOrderChangedFromBooking(order.id);
        } catch (GetShopBeanException ex) {
            // Nothing to do, this happens when the order are removed by a named bean manager.
            // In that case the manager should handle the order itself.
        }
    }
    
    public void markOrderForAutoSending(String orderId) {
        ordersToAutoSend.orderIds.add(orderId);
        saveObject(ordersToAutoSend);
    }
    
    public List<String> getOrdersToAutoSend() {
        List<String> toSend = new ArrayList(ordersToAutoSend.orderIds);
        if(toSend.size() > 0) {
            ordersToAutoSend.orderIds.clear();
            saveObject(ordersToAutoSend);
        }
        return toSend;
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
        if(getSession() == null) {
            logPrint("Tried to fetch an order on id: " + orderId + " when session is null.");
            return null;
        }
        orderId = orderId.replaceAll(",", "");
        orderId = orderId.replaceAll("\\.", "");
        
        User user = getSession().currentUser;
        boolean foundOrder = false;
        long foundOrderIncId = -1;
        for (Order order : getAllOrderIncludedVirtualNonFinalized()) {
            String incOrderId = order.incrementOrderId + "";
            if (!order.id.equals(orderId) && !incOrderId.equals(orderId)) {
                foundOrderIncId = order.incrementOrderId;
                continue;
            }
            foundOrder = true;
            finalizeOrder(order);
            String currentSession = getSession().id;
            if (user == null) {
                if (order.session != null && order.session.equals(currentSession)) {
                    return order;
                }
            } else if (user.isAdministrator() || user.isEditor()) {
//                doubleCheckPriceMatrixAndItemsAdded(order);
                return order;
            } else if (order.userId.equals(user.id)) {
                return order;
            }
        }
        
        logPrint("Order with id :" + orderId + " does not exists, or someone with not correct admin rights tries to fetch it, " + foundOrderIncId);
        if(!foundOrder) {
            logPrint("Order does not exists");
        } else {
            if(getSession().currentUser == null) {
                logPrint("Order does exists but current user is null");
            } else {
                logPrint("Order does exists but user: " + getSession().currentUser.fullName + " does not has access to it");
            }
        }
        
        return null;
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
        validateOrder(order);
        if (order == null) {
            order = getByTransactionId(id);
        }
        
        if (order != null) {
            order.status = status;
            if(order.payment != null) {
                order.payment.transactionLog.put(System.currentTimeMillis(), "Changed orderstatus to : " + status);
            }
            saveOrderInternal(order);
        }
    }
    
    @Override
    public void changeOrderStatusWithPassword(String id, int status, String password) throws ErrorException {
        if(!password.equals("gfdsabdf034534BHdgfsdgfs#!")) {
            return;
        }
        
        Order order = orders.get(id);
        
        if(order.status != Order.Status.PAYMENT_COMPLETED && (status == Order.Status.NEEDCOLLECTING || status == Order.Status.PAYMENT_FAILED)) {
            order.status = status;
            if(order.payment != null) {
                order.payment.transactionLog.put(System.currentTimeMillis(), "Changed orderstatus to : " + status);
            }
            saveOrderInternal(order);
        }
    }
    
    @Override
    public Order getOrderByincrementOrderId(Integer id) throws ErrorException {
        for (Order order : getAllOrderIncludedVirtualNonFinalized()) {
            if(order == null) {
                continue;
            }
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

    
    @Override
    public List<CartTax> getTaxes(Order order) throws ErrorException {
        return order.cart.getCartTaxes();
    }
    
    private Order createOrderInternally(Address address, boolean dummy) throws ErrorException {
        Cart cart = cartManager.getCart();
        cart.clearDisabledItems();
        cart.address = address;
        
        Order order = new Order();
        order.createdDate = new Date();
        order.cart = cart.clone();
        order.reference = cart.reference;
        order.overrideAccountingDate = cart.overrideDate;
        
        //What about orders that is not supposed to be sent, why an address then?
//        if (order.cart == null || order.cart.address == null) {
//            throw new ErrorException(53);
//        }
        
        finalizeCart(order.cart);
        
        if (!dummy) {
            order.incrementOrderId = getNextIncrementalOrderId();
        } else {
            order.incrementOrderId = -1;
        }
        
        order.setOverridePricesFromCartItem();
        
        order.doFinalize();
        if (!dummy) {
            feedGrafana(order);
        }
        
        return order;
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
//        for (Order order : orders.values()) {
//            
//            if (!order.useForStatistic()) {
//                continue;
//            }
//            
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(order.createdDate);
//            if (year != null && cal.get(Calendar.YEAR) != year) {
//                continue;
//            }
//            if (month != null && cal.get(Calendar.MONTH) != (month-1)) {
//                continue;
//            }
//            if (week != null && cal.get(Calendar.WEEK_OF_YEAR) != week) {
//                continue;
//            }
//            if (day != null && cal.get(Calendar.DAY_OF_YEAR) != day) {
//                continue;
//            }
//            
//            if(type != null) {
//                if(order.payment != null && order.payment.paymentType != null && !order.payment.paymentType.equals(type)) {
//                    continue;
//                }
//            }
//            
//            amount += cartManager.calculateTotalCost(order.cart);
//        }

        return amount;
    }
    
    @Override
    public Map<String, List<Statistic>> getMostSoldProducts(int numberOfProducts) {
        Map<String, Integer> counts = new HashMap();
//        
//        for (Order order : orders.values()) {
//            
//            if (order.cart == null) {
//                continue;
//            }
//            
//            for (CartItem item : order.cart.getItems()) {
//                Integer oldCount = counts.get(item.getProduct().id);
//                if (oldCount == null) {
//                    oldCount = 0;
//                }
//                
//                oldCount += item.getCount();
//                counts.put(item.getProduct().id, oldCount);
//            }
//        }
//        
//        counts = sortByValue(counts);
//        
//        Map<String, List<Statistic>> retMap = new HashMap();
//        int i = 0;
//        for (String productId : counts.keySet()) {
//            List<Statistic> statistics = new ArrayList();
//            statistics.addAll(createStatistic(productId));
//            
//            retMap.put(productId, statistics);
//            
//            i++;
//            if (i >= numberOfProducts) {
//                break;
//            }
//            
//            
//        }
//        
        return new HashMap();
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
    
    
    public List<Order> getAllOrderNotTransferredToAccounting() {
        return orders.values().stream()
                .filter(order -> !order.transferredToAccountingSystem)
                .filter(order -> !order.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Depricated 
     * 
     * @return 
     */
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
        
        try {
            updateOrderChangedFromBooking(orderId);
        } catch (GetShopBeanException ex) {
            // Nothing to do, this happens when the order are removed by a named bean manager.
            // In that case the manager should handle the order itself.
        }          
    }

  
    @Override
    public void updatePriceForOrderLine(String cartItemId, String orderId, double price) {
        Order order = getOrder(orderId);
        if (order == null) {
            return;
        }
        
        if (order.closed) {
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
    
    @Override
    public Double getTotalForOrderInLocalCurrencyById(String orderId) {
        Order order = getOrderSecure(orderId);
        return order.getTotalAmountLocalCurrency();
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
    public void checkForOrdersToAutoPay(int daysToTryAfterOrderHasStarted) throws ErrorException {
        for(Order order : orders.values()) {
            if(!orderNeedAutoPay(order, daysToTryAfterOrderHasStarted)) {
                continue;
            }
            User user = userManager.getUserById(order.userId);
            boolean expired = false;
            for(UserCard card : user.savedCards) {
                if(card.isExpired()) {
                    expired = true;
                    continue;
                }
                expired = false;
                order.payment.transactionLog.put(System.currentTimeMillis(), "trying autopay for order: " + order.incrementOrderId);
                
                        
                if(!frameworkConfig.productionMode) {
                    logPrint("Tried autopay with card: " + card.savedByVendor + " - " + card.mask);
                    continue;
                }

                
                try {
                    if(card.savedByVendor.equals("DIBS")) {
                        if(order.payment != null && order.payment.paymentType != null && order.payment.paymentType.toLowerCase().contains("dibs")) {
                            dibsManager.payWithCard(order, card);
                        }
                    }
                    if(card.savedByVendor.equals("stripe")) {
                        if(order.payment != null && order.payment.paymentType != null && order.payment.paymentType.toLowerCase().contains("stripe")) {
                            stripeManager.chargeOrder(order.id, card.id);
                        }
                    }
                    if(card.savedByVendor.equals("EPAY")) {
                        epayManager.payWithCard(order, card);
                    }
                    if(order.status == Order.Status.PAYMENT_FAILED) {
                        notifyAboutFailedPaymentOnOrder(order);
                    }
                }catch(Exception e) {
                    order.payment.transactionLog.put(System.currentTimeMillis(), "Fatal error when trying autopay.");
                    saveOrder(order);
                }
            }
            if(expired) {
                order.payment.transactionLog.put(System.currentTimeMillis(), "no valid cards found (maybe: expired).");
                order.status = Order.Status.PAYMENT_FAILED;
                saveOrder(order);
            }
        }
    }

    public void notifyAboutFailedPaymentOnOrder(Order order) {
        logPrint("Need to notify about failed payment");
    }

    private boolean orderNeedAutoPay(Order order, int daysToTryAfterOrderHasStarted) {
        if(order == null || order.cart == null) {
            return false;
        }
        if(order.cart.getTotal(true) <= 0) {
            return false;
        }
        if(order.status == Order.Status.PAYMENT_FAILED) {
            return false;
        }
        if(order.status >= Order.Status.PAYMENT_COMPLETED && order.status != Order.Status.NEEDCOLLECTING) {
            return false;
        }
        if(order.notChargeYet()) {
           return false; 
        }
        if(order.triedAutoPay()) {
            return false;
        }

        //Order has started, its too late.
        if(!order.id.equals("71e5aa87-0489-476b-b5f8-d4315a22fe6f")) {

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, (daysToTryAfterOrderHasStarted * -1));
            for(CartItem item : order.cart.getItems()) {
                if(item.startDate != null && new Date().after(item.startDate)) {
                    boolean isAfter = yesterday.getTime().after(order.rowCreatedDate);
                    if(order.chargeAfterDate != null && order.chargeAfterDate.after(order.rowCreatedDate)) {
                        isAfter = yesterday.getTime().after(order.chargeAfterDate);
                    }
                    if(isAfter) {
                        Store store = storeManager.getMyStore();
                        try {
                            if(!order.warnedNotAbleToPay) {
                                if(store.configuration.accountingEmailAdress != null && !store.configuration.accountingEmailAdress.isEmpty()) {
                                    String message = "Order "  + order.incrementOrderId + " where not able to capture after tried : " + daysToTryAfterOrderHasStarted + " days";
                                    messageManager.sendMail(store.configuration.accountingEmailAdress, store.configuration.accountingEmailAdress, "Failed to capture on card", message, store.configuration.accountingEmailAdress, store.configuration.accountingEmailAdress);
                                }
                                order.warnedNotAbleToPay = true;
                                saveOrder(order);
                            }
                        }catch(Exception e) {
                            logPrintException(e);
                        }
                        return false;
                    }
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
                .filter(filterByOrderIdsInExtra(filterOptions))
                .collect(Collectors.toList());
        
        if (filterOptions.removeNullOrders) {
            allOrders = allOrders.stream()
                    .filter(o -> !o.isNullOrder())
                    .collect(Collectors.toList());
        }
        
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
        FilteredData result = pageIt(allOrders, filterOptions);
        return result;
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
            generateKid(order);
            String localCurrency = getLocalCurrencyCode();
            
            if (localCurrency.equalsIgnoreCase(order.currency)) {
                order.currency = null;
            }
        }
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

      public Payment getUserPrefferedPaymentMethodOnly(String userId) {
        User user = userManager.getUserById(userId);
        
        if (user == null)
            return null;
        
        if (user.preferredPaymentType == null || user.preferredPaymentType.isEmpty()) {
            return null;
        }
        
        String paymentId = user.preferredPaymentType;
        return createPayment(paymentId);
    }

    
    public Payment getUserPrefferedPaymentMethod(String userId) {
        User user = userManager.getUserById(userId);
        
        if (user == null)
            return null;
        
        if (user.preferredPaymentType == null || user.preferredPaymentType.isEmpty()) {
            return getStorePreferredPayementMethod();
        }
        
        Application paymentApplication = applicationPool.getApplication(user.preferredPaymentType);
        if (paymentApplication != null) { 
            Payment payment = new Payment();
            payment.paymentType = "ns_" + paymentApplication.id.replace("-", "_") + "\\" + paymentApplication.appName;
            payment.paymentId = paymentApplication.id;
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
                payment.paymentId = paymentApplication.id;
                return payment;
            }
        }
        
        return null;
    }

    @Override
    public void sendReciept(String orderId, String email) {
        sendRecieptWithText(orderId, email, null, null);
    }
    
    @Override
    public void sendRecieptWithText(String orderId, String email, String subject, String text) {
        Order order = getOrder(orderId);
        if (order != null) {
            messageManager.sendInvoiceForOrder(orderId, email, subject, text);
            order.closed = true;
            order.payment.transactionLog.put(System.currentTimeMillis(), "Invoice or receipt sent to " + email);

            OrderShipmentLogEntry logentry = new OrderShipmentLogEntry();
            logentry.address = email;
            logentry.type = OrderShipmentLogEntry.Type.email;
            logentry.date = new Date();
            
            order.shipmentLog.add(logentry);
            saveObject(order);
        }
    }

    @Override
    public void markAsInvoicePayment(String orderId) {
        Order order = getOrderSecure(orderId);
        order.payment.paymentType = "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment";
        saveOrder(order);
    }

    private void finalizeOrder(Order order) {
        updateAddressIfNotClosed(order);
        
        List<Order> ordersToFinalise = new ArrayList();
        ordersToFinalise.add(order);
        finalize(ordersToFinalise);
        order.correctStartEndDate();
    }

    
    
    private void updateAddressIfNotClosed(Order order) {
        if (!order.closed && order.userId != null && !order.userId.isEmpty() && order.cart != null) {
            try {
                if(order.cart.address == null) {
                    order.cart.address = new Address();
                }
                
                User user = userManager.getUserById(order.userId);
                if (user == null || user.address == null)
                    return;
                
                if(user.address != null) {
                    user.address.emailAddress = user.emailAddress;
                    user.address.prefix = user.prefix;
                    user.address.phone = user.cellPhone;
                    user.address.fullName = user.fullName;
                }
                
                
                if (order.cart.address.isSame(user, user.address)) {
                    return;
                }
                
                order.cart.address = (Address)user.address.clone();
                if (order.cart.address.fullName == null || order.cart.address.fullName.isEmpty()) {
                    order.cart.address.fullName = user.fullName;
                }
                if (order.cart.address.co == null || order.cart.address.co.isEmpty() && user.address != null) {
                    order.cart.address.co = user.address.co;
                }
                
                saveObject(order);
            } catch (CloneNotSupportedException ex) {
                java.util.logging.Logger.getLogger(OrderManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void feedGrafanaPaymentAmount(Double amountPaid) {
        Application ecommerceSettings = storeApplicationPool.getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");

        String currency = ecommerceSettings.getSetting("currencycode");
        if(currency == null || currency.isEmpty()) {
            currency = "NOK";
        }
        
        
        HashMap<String, Object> toAdd = new HashMap();
        toAdd.put("amount", (Number)amountPaid);
        toAdd.put("storeid", (String)storeId);
        toAdd.put("currency", (String)currency);
        
        GrafanaFeederImpl feeder = new GrafanaFeederImpl();
        grafanaManager.addPoint("webdata", "orderpayment", toAdd);
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
        
        GrafanaFeederImpl feeder = new GrafanaFeederImpl();
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

    @Override
    public List<CartItemDates> getItemDates(Date start, Date end) {
        List<CartItemDates> toreturn = new ArrayList();
        List<String> ordersAdded = new ArrayList();
        for(Order order : orders.values()) {
            if(order.cart == null) {
                continue;
            }
            for(CartItem item : order.cart.getItems()) {
                if(item.startDate == null || item.endDate == null) {
                    continue;
                }
                
                
                CartItemDates res = new CartItemDates();
                res.start = item.startDate;
                res.end = item.endDate;
                res.item = item.getCartItemId();
                res.externalId = item.getProduct().externalReferenceId;
                res.price = item.getProduct().priceExTaxes * item.getCount();
                res.metaData = item.getProduct().additionalMetaData;
                res.orderId = (int)order.incrementOrderId;

                toreturn.add(res);
                ordersAdded.add(order.id);
            }
        }
        return toreturn;
    }

    public boolean orderExists(String orderId) {
        return orders.containsKey(orderId);
    }

    public void deleteAllOrders() {
     //Delete all orders.
        List<Order> allOrders = getOrders(null, null, null);
        for(Order order : allOrders) {
            forceDeleteOrder(order);
        }
        
    }

    @Override
    public boolean payWithCard(String orderId, String cardId) throws Exception {
        Order order = getOrder(orderId);
        
        User user = userManager.getUserById(order.userId);
        UserCard cardToUse = null;
        for(UserCard card : user.savedCards) {
            if(card.id.equals(cardId)) {
                cardToUse = card;
            }
        }
        
        if(cardToUse == null) {
            order.payment.transactionLog.put(System.currentTimeMillis(), "Card with id :  " + cardId + " does not exists, can not continue.");
            return false;
        }
        
        order.payment.transactionLog.put(System.currentTimeMillis(), "Trying to extract with saved card: " + cardToUse.card + " expire: " + cardToUse.expireMonth + "/" + cardToUse.expireYear + " (" + cardToUse.savedByVendor + ")");
        order.payment.triedAutoPay.add(new Date());
        saveOrder(order);
        
        boolean res = false;
        if(cardToUse.savedByVendor.equalsIgnoreCase("dibs")) {
            res = dibsManager.payWithCard(order, cardToUse);
        } else if(cardToUse.savedByVendor.equalsIgnoreCase("epay")) {
            res = epayManager.payWithCard(order, cardToUse);
        } else if(cardToUse.savedByVendor.equalsIgnoreCase("stripe")) {
            res = stripeManager.chargeOrder(order.id, cardToUse.id);
        } else {
            order.payment.transactionLog.put(System.currentTimeMillis(), "Pay with saved card is not supported by this vendor: " + cardToUse.card + " expire: " + cardToUse.expireMonth + "/" + cardToUse.expireYear + " (" + cardToUse.savedByVendor + ")");
        }
        return res;
    }

    @Override
    public Payment getMyPrefferedPaymentMethod() {
        return getUserPrefferedPaymentMethod(getSession().currentUser.id);
    }

    @Override
    public void forceDeleteOrder(String orderId, String password) {
        if(!password.equals("fdsafnbbo45453gbsdsdgfHTRYTnvnvvqerqw98ngvdjsgndfl8345()")) {
            return;
        }
        Order object = orders.get(orderId);
        forceDeleteOrder(object);
    }

    @Override
    public AccountingDetails getAccountingDetails() throws ErrorException {
        Application settings = storeApplicationPool.getApplication("70ace3f0-3981-11e3-aa6e-0800200c9a66");
        AccountingDetails details = new AccountingDetails();
        details.accountNumber = settings.getSetting("accountNumber");
        details.address = settings.getSetting("address");
        details.city = settings.getSetting("city");
        details.companyName = settings.getSetting("companyName");
        details.contactEmail = settings.getSetting("contactEmail");
        details.dueDays = Integer.parseInt(settings.getSetting("duedays"));
        details.vatNumber = settings.getSetting("vatNumber");
        details.webAddress = settings.getSetting("webAddress");
        details.type = settings.getSetting("type");
        details.currency = settings.getSetting("currency");
        details.iban = settings.getSetting("iban");
        details.swift = settings.getSetting("swift");

        return details;
    }
    
    @Override
    public List<Order> getOrdersPaid(String paymentId, String userId, Date from, Date to) {
        List<Order> retOrderStream = orders.values()
                .stream()
                .filter(order -> order.paymentDate != null && order.paymentDateWithin(from, to))
                .filter(order -> userId.equals("") || order.markedAsPaidByUserId.equals(userId))
                .filter(order -> paymentId.equals("") || order.payment.paymentType.equals(paymentId))
                .collect(Collectors.toList());
        

        retOrderStream.stream()
                .forEach(order -> finalizeOrder(order));
        
        return retOrderStream;
    }

    @Override
    public void printInvoice(String orderId, String printerId) {
        Order order = getOrder(orderId);
        printOrderToPrinter(order, printerId);
    }

    public void printOrderToPrinter(Order order, String printerId) throws ErrorException, RuntimeException {
        Printer printer = storePrintManager.getPrinter(printerId);
        
        if (printer == null)
            throw new RuntimeException("Printer not found");
        
        if (printer.type.equals("receipt")) {
            PrintJob printJob = new PrintJob();
            printJob.printerId = printerId;
            printJob.content = new ReceiptGenerator(order, getSession().currentUser, getAccountingDetails()).getContent();
            printJob.convertAdaFruit();
            printManager.addPrintJob(printJob);
        }
    }

    public void changeOrderCreatedByManagerName(String id, String managerName) {
        Order order = this.getOrder(id);
        if (order != null) {
            order.createByManager = managerName;
            saveObject(order);
        }
    }

    public void saveVirtalOrder(VirtualOrder virtualOrder) {
        saveObject(virtualOrder);
        virtualOrders.put(virtualOrder.id, virtualOrder);
    }

    private List<Order> getAllOrderIncludedVirtualNonFinalized() {
        List<Order> retval = new ArrayList();
        retval.addAll(new ArrayList(orders.values()));
        for(VirtualOrder vord : virtualOrders.values()) {
            retval.add(vord.order);
        }
        return retval;
    }
    
    @Override
    public void addClosedPeriode(ClosedOrderPeriode closed) {
        boolean alreadyClosed = closedPeriodes.values().stream()
                .filter(periode -> periode.paymentTypeId.equals(closed.paymentTypeId))
                .filter(periode -> periode.interCepts(closed.startDate, closed.endDate))
                .count() > 0;
        
        if (alreadyClosed) {
            throw new ErrorException(1038);
        }
        
        saveObject(closed);
        closedPeriodes.put(closed.id, closed);
    }

    private void validateOrder(Order incomeOrder) {
        Order inMemory = null;
        
        if (incomeOrder.id != null && !incomeOrder.id.isEmpty()) {
            inMemory = getOrderSecure(incomeOrder.id);
        }
        
        if (incomeOrder.paymentDate == null)
            return;
        
        if (inMemory != null && inMemory.isPaymentDate(incomeOrder.paymentDate)) {
            return;
        }
        
        boolean inClosedPeriode = closedPeriodes.values().stream()
                .filter(periode -> periode.paymentTypeId.equals(incomeOrder.payment.paymentId))
                .filter(periode -> periode.within(incomeOrder.paymentDate))
                .count() > 0;
                
        if (inClosedPeriode) {
            throw new ErrorException(1039);
        }
        
    }

    @Override
    public List<Order> getAllUnpaidInvoices() {
        String invoicePaymentAppId = "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment";
        return getAllUnpaid(invoicePaymentAppId);
    }

    private void removeCredittedOrders(List<Order> retOrders) {
        List<Order> toRemove = new ArrayList();
        
        for (Order order : retOrders) {
            if (order.creditOrderId.isEmpty())
                continue;
            
            double credittedSum = order.creditOrderId.stream()
                    .map(id -> getOrder(id))
                    .mapToDouble(iorder -> getTotalAmount(iorder))
                    .sum();
                    
            
            double sum = getTotalAmount(order);
            double test = credittedSum + sum;
            
            if (test == 0) {
                toRemove.add(order);
            }
        }
        
        retOrders.removeAll(toRemove);
    }

    @Override
    public List<Order> getAllUnpaid(String paymentMethod) {
        List<Order> retOrders = orders.values().stream()
                .filter(order -> !order.isCreditNote)
                .filter(order -> order.payment != null && order.payment.paymentType.equals(paymentMethod))
                .filter(order -> order.status != 7)
                .collect(Collectors.toList());
                
        removeCredittedOrders(retOrders);
        
        finalize(retOrders);
        
        return retOrders;
    }
 
   @Override
    public Order mergeAndCreateNewOrder(String userId, List<String> orderIds, String paymentMethod, String note) {
        User user = userManager.getUserById(userId);
        
        if (user == null) {
            throw new ErrorException(8);
        }
        
        if (orderIds.isEmpty())
            return null;
       
        orderIds.stream().forEach(orderId -> { 
            Order order = getOrder(orderId);
            Order creditedOrder = createCreatditOrder(orderId, "ordermanager_merged_order"); 
            creditedOrder.overrideAccountingDate = new Date();
            markAsPaid(orderId, new Date(), order.getPaidRest()); 
            markAsPaid(creditedOrder.id, new Date(), order.getPaidRest());
            order.closed = true;
            saveObject(order);
        });
        
        cartManager.clear();
        
        orderIds.stream()
                .map(orderId -> getOrder(orderId))
                .forEach(order -> { cartManager.getCart().addCartItems(order.cart.getItems()); });
        
        if (cartManager.isCartConflictingWithClosedPeriode()) {
            cartManager.getCart().overrideDate = new Date();
        }
        
        Order newOrder = createOrder(user.address);
        newOrder.userId = userId;
        newOrder.payment = createPayment(paymentMethod);
        newOrder.invoiceNote = note;
        newOrder.createdBasedOnOrderIds = orderIds;
        newOrder.overrideAccountingDate = new Date();
        
        if (newOrder.cart != null) {
            newOrder.cart.reference = "ordermanager_merged_order";
        }
        
        setCompanyAsCartIfUserAddressIsNullAndUserConnectedToACompany(newOrder, userId);
        
        if (newOrder.cart.address == null || newOrder.cart.address.address == null || newOrder.cart.address.address.isEmpty()) {
            newOrder.cart.address = user.address;
        }
        
        if (newOrder.cart.address != null)
            newOrder.cart.address.fullName = user.fullName;
        
        saveOrderInternal(newOrder);
        
        finalizeOrder(newOrder);
        
        addOrderToBooking(newOrder);
        
        return newOrder;
    }
    
    public void setCompanyAsCartIfUserAddressIsNullAndUserConnectedToACompany(Order order, String userId) {
        User user = userManager.getUserById(userId);
        if (order.cart.address == null || order.cart.address.address == null || order.cart.address.address.isEmpty()) {
            if (!user.company.isEmpty()) {
                Company company = userManager.getCompany(user.company.get(0));
                order.cart.address = company.address;
                if (order.cart.address == null) {
                    order.cart.address = new Address();
                }
                order.cart.address.fullName = company.name;
            }
        }
    }

    private Payment createPayment(String paymentId) {
        Application paymentApplication = applicationPool.getApplication(paymentId);
        
        if (paymentApplication != null) { 
            Payment payment = new Payment();
            payment.paymentType = "ns_" + paymentApplication.id.replace("-", "_") + "\\" + paymentApplication.appName;
            payment.paymentId = paymentApplication.id;
            return payment;
        }
        
        return null;
    }

    private void emptyPullServerQueue() {
        if(queuedEmptied) {
            return;
        }
        try {
            List<PullMessage> msgs = getShopPullService.getMessages("getshop_all_message_for_store_to_receive", storeId);
            for (PullMessage msg : msgs) {
                getShopPullService.markMessageAsReceived(msg.id, storeId);
            }
            queuedEmptied = true;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(OrderManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<String> getPaymentMethodsThatHasOrders() {
        HashMap<String, Integer> maps = new HashMap();
        for(Order order : orders.values()) {
           if(order.payment != null && order.payment.paymentType != null) {
               String paymentId = order.payment.getPaymentTypeId();
               maps.put(paymentId, 1);
           }
        }
        return new ArrayList(maps.keySet());
    }

    @Override
    public void checkForOrdersFailedCollecting() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -30);
        Date past = cal.getTime();
        for(Order order : orders.values()) {
            if(order.status == Order.Status.NEEDCOLLECTING && order.needCollectingDate != null && !order.warnedNotAbleToCapture && order.incrementOrderId > 0) {
                if(past.after(order.needCollectingDate)) {
                    messageManager.sendMessageToStoreOwner("Order failed to be collected in 30 minutes, order id: " + order.incrementOrderId, "Payment warning");
                    messageManager.sendErrorNotificationToEmail("pal@getshop.com","Order failed to be collected in 30 minutes, order id: " + order.incrementOrderId, null);
                    order.warnedNotAbleToCapture = true;
                    saveOrder(order);
                }
            }
        }
    }
    public List<Order> getOrdersToTransferToAccount(Date endDate) {
        updateTransferToAccountingDate();
        
        List<Order> retOrders = orders.values()
                .stream()
                .filter(order -> order.transferredToAccountingSystem == null || !order.transferredToAccountingSystem)
                .filter(order -> order.transferToAccountingDate != null)
                .filter(order -> !order.isNullOrder())
                .filter(order -> order.isTransferBefore(endDate))
                .collect(Collectors.toList());
        
        if (retOrders == null) {
            return new ArrayList();
        }
        
        return retOrders;
    }

    private void updateTransferToAccountingDate() {
        orders.values()
                .stream()
                .filter(order -> !order.transferredToAccountingSystem)
                .forEach(order -> {
                    boolean orderChanged = paymentManager.handleOrder(order);
                    if (orderChanged) {
                        saveObject(order);
                    }
                });
    }

    

    private void checkChargeAfterDate() {
        for(Order ord : this.orders.values()) {
            if(ord.chargeAfterDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(ord.rowCreatedDate);
                int createdYear = cal.get(Calendar.YEAR);
                int createdMonth = cal.get(Calendar.MONTH);
                if(createdYear != 2018 || createdMonth != 0) {
                    continue;
                }
                
                
                cal.setTime(ord.chargeAfterDate);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                if(month == 2 && year == 2018 && day == 1) {
                    logPrint(ord.rowCreatedDate + " : " + ord.chargeAfterDate + " : " + day + "." + month+"."+year);
                    cal.set(Calendar.MONTH, 1);
                    ord.chargeAfterDate = cal.getTime();
                    saveObject(ord);
                }
            }
        }
    }


    private Predicate<? super Order> filterByOrderIdsInExtra(FilterOptions filterOptions) {
        if (filterOptions.extra.get("orderids") == null) {
            return order -> order != null;
        }
        
        List<String> orderIds = Arrays.asList(filterOptions.extra.get("orderids").split(","));
        
        if (orderIds.isEmpty())
            return order -> order != null;
        
        return order -> orderIds.contains(order.id);
    }

    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList();
        for(Order order : orders.values()) {
            orderList.add(order);
        }
        return orderList;
    }
    

    @Override
    public void deleteOrder(String orderId) {
        Order order = getOrder(orderId);
        if (order == null)
            return;
        
        if (order.closed)
            return;
        
        if (order.createdBasedOnOrderIds != null && !order.createdBasedOnOrderIds.isEmpty()) {
            addCreditNotesToBookings(order.createdBasedOnOrderIds);
        }
        
        revertOrderLinesToPreviouseState(order);
        
        if (order.isCreditNote) {
            orders.values()
                    .stream()
                    .filter(o -> o.creditOrderId.contains(orderId))
                    .forEach(o -> {
                        o.creditOrderId.remove(orderId);
                        saveObject(o);
                    });
        }
        
        order.virtuallyDeleted = true;
        order.cart.clear();
        saveObject(order);
        
        try {
            updateOrderChangedFromBooking(order.id);
        } catch (GetShopBeanException ex) {
            // Nothing to do, this happens when the order are removed by a named bean manager.
            // In that case the manager should handle the order itself.
        }
    }
    
    private void updateOrderChangedFromBooking(String orderId) {
        List<String> multiLevelNames = database.getMultilevelNames("PmsManager", storeId);
        for (String multilevelName : multiLevelNames) {
            PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(multilevelName, PmsManager.class);
            if(pmsManager != null) {
                pmsManager.orderChanged(orderId);
            }
        }
    }

    private void revertOrderLinesToPreviouseState(Order order) {
        List<String> newOrders = new ArrayList();
        
        order.createdBasedOnOrderIds.stream()
                .forEach(id -> {
                    Order gOrder = getOrder(id);
                    Order newOrder = gOrder.jsonClone();
                    newOrder.closed = false;
                    newOrder.overrideAccountingDate = new Date();
                    unMarkPaidOrder(newOrder);
                    orders.put(newOrder.id, newOrder);
                    newOrders.add(newOrder.id);
                });
        
        addOrdersToBookings(newOrders);
        
        for (String conferenceId : order.conferenceIds) {
            PosConference conference = posManager.getPosConference(conferenceId);
            if (conference != null) {
                order.getCartItems()
                    .stream()
                    .filter(o -> o.conferenceId.equals(conferenceId))
                    .forEach(cartItem -> {
                        try {
                            posManager.addToTab(conference.tabId, (CartItem) cartItem.clone());
                        } catch (CloneNotSupportedException ex) {
                            java.util.logging.Logger.getLogger(OrderManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
            }
        }
    }

    public HashMap<String, List<CartItem>> groupItemsOnOrder(Cart cart) {
        HashMap<String, List<CartItem>> toReturn = new HashMap();
        for(CartItem item : cart.getItems()) {
            List<CartItem> items = toReturn.get(item.getProduct().externalReferenceId);
            if(items == null) {
                items = new ArrayList();
            }
            items.add(item);
            toReturn.put(item.getProduct().externalReferenceId, items);
        }
        return toReturn;
    }

    public Double getTotalAmountForUser(String id) {
        double total = 0.0;
        for(Order ord : orders.values()) {
            if(ord == null || ord.userId == null) {
                continue;
            }
            if(ord.userId.equals(id)) {
                total += getTotalAmountExTaxes(ord);
            }
        }
        return total;
    }

    @Override
    public List<OrderResult> getOrdersByFilter(OrderFilter filter) {
        
        List<Order> ordersToReturn = new ArrayList();
        OrderFiltering filtering = new OrderFiltering();
        filtering.setOrders(new ArrayList(orders.values()));
        
        if(filter.searchWord != null && !filter.searchWord.isEmpty()) {
            ordersToReturn = searchForOrders(filter.searchWord, null, null);
        } else {
            ordersToReturn = filtering.filterOrders(filter);
        }
        
        List<OrderResult> orderFilterResult = new ArrayList();
        for(Order ord : ordersToReturn) {
            if(ord.isEmpty()) {
                continue;
            }
            OrderResult res = new OrderResult();
            res.setOrder(ord);
            User usr = userManager.getUserByIdUnfinalized(ord.userId);
            if(usr != null) {
                res.user = usr.fullName;
            }
            orderFilterResult.add(res);
        }
        
        orderFilterResult.add(filtering.sum(orderFilterResult));
        
        return orderFilterResult;
    }

    @Override
    public Order getOrderByincrementOrderIdAndPassword(Integer id, String password) throws ErrorException {
        if(!password.equals("fdsafd4e3453ngdgdf")) {
            return null;
        }
        return getOrderByincrementOrderId(id);
    }

    @Override
    public Order getOrderWithIdAndPassword(String orderId, String password) throws ErrorException {
        if(!password.equals("gfdsg9o3454835nbsfdg")) {
            return null;
        }
        
        try {
            Integer incordid = new Integer(orderId);
            return getOrderByincrementOrderId(incordid);
        }catch(Exception e) {
            
        }
        
        return getOrderSecure(orderId);
    }

    @Override
    public Double getRestToPay(Order order) {
        return order.getTotalAmount() - order.getTransactionAmount();
    }

    @Override
    public void markAsPaidWithPassword(String orderId, Date date, Double amount, String password) {
        if(!password.equals("fdsvb4354345345")) {
            return;
        }
        Order order = getOrder(orderId);
        markAsPaid(orderId, date, amount);
    }

    @Override
    public String getEhfXml(String orderId) {
        AccountingDetails details = invoiceManager.getAccountingDetails();
        Order order = getOrder(orderId);
        invoiceManager.checkForNullNameOnProduct(order);
        invoiceManager.generateKidOnOrder(order);
        
        User user = userManager.getUserById(order.userId);
        EhfXmlGenerator generator = new EhfXmlGenerator(order, details, user);
        String xml = "";
        
        try {
            xml = generator.generateXml(storeManager.isProductMode());
        } catch (ErrorException ex) {
            messageManager.sendErrorNotification("There was an error while validating the EHF, please investigate. <br/>OrderId: " + orderId, ex);
            return "failed";
        }
        
        return xml;
    }

    @Override
    public void closeOrder(String orderId, String reason) {
        if (getSession() != null && getSession().currentUser != null) {
            reason += ", done by: " + getSession().currentUser.fullName;
        }
        Order order = getOrder(orderId);
        order.closed = true;
        order.payment.transactionLog.put(System.currentTimeMillis(), reason);
        saveObject(order);
    }

    @Override
    public List<String> isConfiguredForEhf() {
        List<String> errors = new ArrayList();
        
        AccountingDetails details = invoiceManager.getAccountingDetails();
        
        if (details.vatNumber.isEmpty()) 
            errors.add("Vat number not configured for invoice");
        
        if (details.companyName == null || details.companyName.isEmpty())
            errors.add("Company name not set");
            
        if (details.address.isEmpty())
            errors.add("Street address not connfigured for invoice");
        
        if (details.postCode.isEmpty())
            errors.add("Postcode not configured for invoice");
        
        if (details.accountNumber.isEmpty())
            errors.add("Account number not configured for invoice");
        
        if (details.contactEmail.isEmpty())
            errors.add("Email not configured for for invoice");
            
        return errors;
    }

    @Override
    public void registerSentEhf(String orderId) {
        Order order = getOrder(orderId);
        
        EhfSentLog log = new EhfSentLog();
        log.orderId = orderId;
        log.userId = getSession().currentUser.id;
        log.vatNumber = userManager.getUserById(order.userId).companyObject.vatNumber;
        saveObject(log);
        
        OrderShipmentLogEntry logentry = new OrderShipmentLogEntry();
        logentry.address = log.vatNumber;
        logentry.type = OrderShipmentLogEntry.Type.ehf;
        logentry.date = new Date();
        order.shipmentLog.add(logentry);
        saveOrder(order);
    }

    public List<EhfSentLog> getEhfSentLog(Date start, Date end) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", EhfSentLog.class.getCanonicalName());
        query.put("rowCreatedDate", BasicDBObjectBuilder.start("$gte", start).add("$lte", end).get());

        List<DataCommon> datas = database.query(OrderManager.class.getSimpleName(), storeId, query);
        ArrayList result = new ArrayList(datas);
        
        Collections.sort(result, new Comparator<DataCommon>(){
             public int compare(DataCommon o1, DataCommon o2){
                 return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
             }
        });
        
        return result;
    }
    public void markOrderAsTransferredToAccounting(String orderId) {
        Order order = getOrderSecure(orderId);
        
        if (order.transferredToAccountingSystem) {
            return;
        }
        
        order.transferredToAccountingSystem = true;
        saveOrderInternal(order);
    }

    public void resetTransferToAccount(String orderId) {
        Order order = getOrder(orderId);
        order.resetTransferToAccounting();
        saveOrderInternal(order);
    }
    
    @Override
    public void postProcessMessage(Method executeMethod, Object[] argObjects) {
        blockManuallyPaymentMarkingForPaymentMethodsThatShouldNotDoThat(executeMethod, argObjects);
        overridePaymentDate(executeMethod, argObjects);
    }

    private void blockManuallyPaymentMarkingForPaymentMethodsThatShouldNotDoThat(Method executeMethod, Object[] argObjects) throws ErrorException {
        if (executeMethod != null && (executeMethod.getName().equals("markAsPaid") || executeMethod.getName().equals("addOrderTransaction"))) {
            String orderId = (String)argObjects[0];
            Order order = getOrder(orderId);
            if (order.payment == null) {
                throw new ErrorException(1052);
            }
            
            // AccruedPayment
            if (order.getPaymentApplicationId().equals("60f2f24e-ad41-4054-ba65-3a8a02ce0190")) {
                throw new ErrorException(1052);
            }
            
            // Samlefaktura
            if (order.getPaymentApplicationId().equals("cbe3bb0f-e54d-4896-8c70-e08a0d6e55ba") && order.getTotalAmount() > 0) {
                throw new ErrorException(1052);
            }
            
            // Verifone
            if (order.getPaymentApplicationId().equals("6dfcf735-238f-44e1-9086-b2d9bb4fdff2") && order.getTotalAmount() > 0) {
//                throw new ErrorException(1052);
            }            
        }
    }

    /**
     * We override the date if someone tries to set a different payment date for cash payments.
     * 
     * @param order
     * @return 
     */
    private boolean shouldOverridePaymentDate(Order order) {
        User user = getSession().currentUser;
        
        if (order.getPaymentApplicationId().equals("565ea7bd-c56b-41fe-b421-18f873c63a8f")) {
            return true;
        }
        
        return false;
    }

    private void overridePaymentDate(Method executeMethod, Object[] argObjects) {
        if (executeMethod != null && executeMethod.getName().equals("markAsPaid")) {
            String orderId = (String)argObjects[0];
            Order order = getOrder(orderId);
            if (shouldOverridePaymentDate(order)) {
                argObjects[1] = new Date();
            }
        }
        
    }

    @Override
    public OrderLight getOrderLight(String orderId) throws ErrorException {
        Order order = getOrder(orderId);
        if (order != null) {
            return new OrderLight(order);
        }
        
        return null;
    }

    public Order getOrderByKid(String kid) {
        for(Order ord : orders.values()) {
            if(ord.kid == null || ord.kid.trim().isEmpty()) {
                continue;
            }
            if(kid.contains(ord.kid)) {
                return ord;
            }
        }
        return null;
    }

    public void markAsPaidWithTransactionType(String orderId, Date date, Double amount, int transactiontype, String refId) {
        markAsPaidWithTransactionTypeInternal(orderId, amount, date, transactiontype, refId, null, null);
    }

    private void markAsPaidWithTransactionTypeInternal(String orderId, Double amount, Date date, int transactiontype, String refId, Double amountInLocalCurrency, Double agio) throws ErrorException {
        Order order = orders.get(orderId);
        if(amount != null && amount != 0.0) {
            String userId = "";
            if(getSession() != null && getSession().currentUser != null) {
                userId = getSession().currentUser.id;
            }
            order.registerTransaction(date, amount, userId, transactiontype, refId, "", amountInLocalCurrency, agio, "");
            feedGrafanaPaymentAmount(amount);
            if(order.isFullyPaid() || order.isCreditNote) {
                markAsPaidInternal(order, date,amount);
                saveOrder(order);
            }
        } else {
            markAsPaidInternal(order, date,amount);
            saveOrder(order);
        }
    }

    @Override
    public List<OrderTransaction> getBankOrderTransactions() {
        List<Order> invoices = orders.values().stream()
                .filter(order -> order.isInvoice())
                .collect(Collectors.toList());
        
        List<OrderTransaction> transactions = new ArrayList();
        for (Order invoiceOrder : invoices) {
            for (OrderTransaction orderTransaction : invoiceOrder.orderTransactions) {
                if (orderTransaction.transferredToAccounting) {
                    continue;
                }
                
                orderTransaction.orderId = invoiceOrder.id;
                transactions.add(orderTransaction);
            }
        }
        
        Collections.sort(transactions, (OrderTransaction a, OrderTransaction b) -> {
            return a.date.compareTo(b.date);
        });

        return transactions;
    }

    public void markTransactionAsTransferredToAccounting(OrderTransaction transaction) {
        Order order = getOrder(transaction.orderId);
        if (order != null) {
            order.orderTransactions.stream()
                    .forEach(t -> {
                        if (t.refId.equals(transaction.refId)) {
                            t.transferredToAccounting = true;
                        }
                    });
            
            saveObject(order);
        }
    }

    @Override
    public void markOrderAsBillabe(String orderId) {
        Order order = getOrder(orderId);
        if (order != null) {
            order.billable = true;
            saveObject(order);
        }
    }

    @Override
    public List<DayIncome> getDayIncomes(Date start, Date end) {
        DayIncomeFilter filter = new DayIncomeFilter();
        filter.start = start;
        filter.end = end;
        filter.ignoreConfig = false;
        
        return getDayIncomesInternal(filter);
    }

    private List<DayIncome> getDayIncomesInternal(DayIncomeFilter filter) {
        List<DayIncome> dayIncomes = new ArrayList();
        
        if (!filter.ignoreFromDatabase) {
            dayIncomes = getDayIncomesFromDatabase(filter.start, filter.end);
            dayIncomes.stream().forEach(o -> o.isFinal = true);
        }
        
        OrderDailyBreaker breaker = new OrderDailyBreaker(getAllOrders(), filter, paymentManager, productManager, getOrderManagerSettings().whatHourOfDayStartADay, getAllFreePosts(), storeOcrManager);
        breaker.breakOrders();
        
        if (breaker.hasErrors()) {
            return breaker.getErrors();
        }
        
        List<DayIncome> newlyBrokenIncome = breaker.getDayIncomes();
        List<DayIncome> fromDatabase = new ArrayList(dayIncomes);
        
        newlyBrokenIncome.removeIf(income -> {
            return isInArray(income, fromDatabase);
        });
        
        newlyBrokenIncome.addAll(dayIncomes);
        
        newlyBrokenIncome.removeIf(o -> {
            long startL = filter.start.getTime();
            long endL = filter.end.getTime() + (1000*60*60*24);
            boolean completlyWithin = startL <= o.start.getTime() && o.end.getTime() <= endL;
            return !completlyWithin;
        });
        
        newlyBrokenIncome.sort((DayIncome a, DayIncome b) -> {
            return a.start.compareTo(b.start);
        });
        
        return newlyBrokenIncome;
    }

    private List<DayIncome> getDayIncomesFromDatabase(Date startDate, Date endDate) {
        long start = startDate.getTime();
        long end = endDate.getTime();
        
        BasicDBObject query = new BasicDBObject();
        query.put("className", DayIncomeReport.class.getCanonicalName());
        
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        List<DayIncomeReport> all = database.query("OrderManager", storeId, query).stream()
                .map(o -> (DayIncomeReport)o)
                .filter(r -> r.deleted == null)
                .collect(Collectors.toList());
        
        List<DayIncome> dayIncomes = all.stream()
                .flatMap(o -> o.incomes.stream())
                .filter(r -> {
                    
                    if(r.dayEntries.isEmpty()) {
                        return false;
                    }
                    
                    long iStart = r.start.getTime();
                    long iEnd = r.end.getTime();
                    
                    boolean startIsWithin = iStart >= start && iStart < end;
                    boolean endIsWithin = iEnd >= start && iEnd < end;
                    boolean everythingIsBetween = iStart >= start && iEnd < end;
                    
                    return startIsWithin || endIsWithin || everythingIsBetween;
                })
                .collect(Collectors.toList());
        
        return dayIncomes;
    }
    
    private DayIncomeReport getReport(Date startDate, Date endDate) {
        long start = startDate.getTime();
        long end = endDate.getTime();
        
        BasicDBObject query = new BasicDBObject();
        query.put("className", DayIncomeReport.class.getCanonicalName());
        return database.query("OrderManager", storeId, query).stream()
                .map(o -> (DayIncomeReport)o)
                .filter(r -> {
                    long iStart = r.start.getTime();
                    long iEnd = r.end.getTime();
                    
                    return iStart <= start && iEnd > end && r.deleted == null;
                })
                .findAny()
                .orElse(null);
        
    }
    
    @Override
    public Long getIncrementalOrderIdByOrderId(String orderId) {
        Order order = getOrder(orderId);
        if (order != null) {
            return order.incrementOrderId;
        }
        
        return 0L;
    }

    @Override
    public List<DayEntry> getDayEntriesForOrder(String orderId) {
        Order order = getOrder(orderId);
        Date start = order.rowCreatedDate;
        Date end = order.rowCreatedDate;
        
        DayIncomeFilter filter = new DayIncomeFilter();
        filter.start = start;
        filter.end = end;
        filter.includePaymentTransaction = true;
        
        List<Order> orders = new ArrayList();
        orders.add(order);
        OrderDailyBreaker breaker = new OrderDailyBreaker(orders, filter, paymentManager, productManager, getOrderManagerSettings().whatHourOfDayStartADay, getAllFreePosts(), storeOcrManager);
        breaker.breakOrders();
        
        return breaker.getDayEntries();
    }

    @Override
    public void resetLastMonthClose(String password, Date start, Date end) {
       if (password != null && password.equals("adfs9a9087293451q2oi4h1234khakslhfasidurh23")) {
            List<DayIncome> dayIncomes = getDayIncomes(start, end);
            
            for (DayIncome income : dayIncomes) {
                BasicDBObject query = new BasicDBObject();
                query.put("incomes.id", income.id);
                
                List<DataCommon> datas = database.query("OrderManager", storeId, query);
                datas.stream().forEach(o -> {
                    resetFreePostsEntries(o);
                    logPrint("Deleted report");
                    deleteObject(o);
                });
            }
            
            OrderManagerSettings settings = getOrderManagerSettings();
            settings.closedTilPeriode = start;

            saveObject(settings);
        }
    }
    
    @Override
    public void closeBankAccount(Date closeDate) {
        Date closePeriodeToDate = closeDate;
        Calendar cal = Calendar.getInstance();
        cal.setTime(closeDate);
        
        OrderManagerSettings settings = getOrderManagerSettings();
        
        // If hour, minute, second is not exaclty the closing date, change time to be in the beginning of the very next day with the accounting closing date.
        if (cal.get(Calendar.HOUR_OF_DAY) != 0 || cal.get(Calendar.MINUTE) != 0 || cal.get(Calendar.SECOND) != 0) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            closePeriodeToDate = cal.getTime();
        }
        
        if (settings.bankAccountClosedToDate.equals(closePeriodeToDate) || closePeriodeToDate.before(settings.bankAccountClosedToDate)) {
            throw new RuntimeException("The periode has already been closed.");
        }
        
        Date now = new Date();
        if (now.before(closePeriodeToDate)) {
            throw new RuntimeException("We can not close down for the future");
        }
        
        settings.bankAccountClosedToDate = closePeriodeToDate;
        saveObject(settings);
    }
    
    @Override
    public void closeTransactionPeriode(Date closeDate) {
        Date closePeriodeToDate = closeDate;
        
        OrderManagerSettings settings = getOrderManagerSettings();
        
        closePeriodeToDate = changeCloseDateToCorrectDate(closePeriodeToDate);
        
        if (settings.closedTilPeriode.equals(closePeriodeToDate) || closePeriodeToDate.before(settings.closedTilPeriode)) {
            throw new RuntimeException("The periode has already been closed.");
        }
        
        Date now = new Date();
        if (now.before(closePeriodeToDate)) {
            throw new RuntimeException("We can not close down for the future");
        }
        
        List<DayIncome> days = getDayIncomes(settings.closedTilPeriode, closeDate);
        
        createAndSaveIncomeReport(settings.closedTilPeriode, closePeriodeToDate, days);
        
        for (DayIncome day : days) {
            for (DayEntry entry : day.dayEntries) {
                if (entry.freePostId != null && !entry.freePostId.isEmpty()) {
                    markFreePostingAsClosed(entry.freePostId);
                } else {
                    markOrderAsTransferredToAccounting(entry.orderId);
                }
            }
        }
        
        closeSegmentsInBookingManager();
        
        settings.closedTilPeriode = closePeriodeToDate;
        saveObject(settings);
    }   

    public Date changeCloseDateToCorrectDate(Date closePeriodeToDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(closePeriodeToDate);
        
        OrderManagerSettings settings = getOrderManagerSettings();
        
        // If hour, minute, second is not exaclty the closing date, change time to be in the beginning of the very next day with the accounting closing date.
        if (cal.get(Calendar.HOUR_OF_DAY) != settings.whatHourOfDayStartADay || cal.get(Calendar.MINUTE) != 0 || cal.get(Calendar.SECOND) != 0) {
            cal.set(Calendar.HOUR_OF_DAY, settings.whatHourOfDayStartADay);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            closePeriodeToDate = cal.getTime();
        }
        return closePeriodeToDate;
    }
    
    public OrderManagerSettings getOrderManagerSettings() {
        BasicDBObject query = new BasicDBObject();
        query.put("className", "com.thundashop.core.ordermanager.data.OrderManagerSettings");
        
        if (this.orderManagerSettings == null) {
            this.orderManagerSettings = database.query("OrderManager", storeId, query).stream()
                    .map(o -> (OrderManagerSettings)o)
                    .findFirst()
                    .orElse(null);
        }
        
        if (this.orderManagerSettings == null) {
            this.orderManagerSettings = new OrderManagerSettings();
            saveObject(this.orderManagerSettings);
        }
        
        if (this.orderManagerSettings.closedTilPeriode == null || this.orderManagerSettings.closedTilPeriode.equals(new Date(0))) {
            this.orderManagerSettings.closedTilPeriode = getStore().rowCreatedDate;
            saveObject(this.orderManagerSettings);
        }
        
        if (this.orderManagerSettings.bankAccountClosedToDate == null || this.orderManagerSettings.bankAccountClosedToDate.equals(new Date(0))) {
            this.orderManagerSettings.bankAccountClosedToDate = getStore().rowCreatedDate;
            saveObject(this.orderManagerSettings);
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.orderManagerSettings.closedTilPeriode);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        
        this.orderManagerSettings.closedTilPeriode = cal.getTime();
                
        return this.orderManagerSettings;
    }

    @Override
    public void saveObject(DataCommon data) throws ErrorException {
        if (data instanceof Order) {
            updateCurrencyForItems((Order)data);
        }
        
        checkAndResetOrderByClosedPeriodeDate(data);
        super.saveObject(data); //To change body of generated methods, choose Tools | Templates.
        
        if (data instanceof Order) {
            updateStock((Order)data);
        }
        
        
    }

    private void checkAndResetOrderByClosedPeriodeDate(DataCommon data) {
        boolean isOrderObject = (data instanceof Order);
        if (!isOrderObject) { 
            return;
        }
        
        Date closedDate = getOrderManagerSettings().closedTilPeriode;

        Order order = (Order)data;
        Order oldOrder = null;
        
        if (order.id != null && !order.id.isEmpty()) {
            oldOrder = (Order)database.getObject(getCredentials(), order.id);
        }
        
        /**
         * We need to make sure that none of the payments registered are in a closed periode where
         * the f-report has been finalized.
         * 
         * for invoices we allow it as long as the bank account has not been closed.
         */
        if (!order.isInvoice()) {
            stopIfNewPaymentDatesConflictWithClosedPeriode(order, getOrderManagerSettings().closedTilPeriode, oldOrder);
        } else {
            stopIfNewPaymentDatesConflictWithClosedPeriode(order, getOrderManagerSettings().bankAccountClosedToDate, oldOrder);
        }
        
        stopIfOverrideDateConflictingClosedDate(order, closedDate, oldOrder);
        
        if (order.overrideAccountingDate != null && (order.overrideAccountingDate.after(closedDate) || order.overrideAccountingDate.equals(closedDate)) && !order.forcedOpen) {
            return;
        }
        
        if (order.needToStopDueToIllegalChangeOfPriceMatrix(oldOrder, closedDate) && !order.forcedOpen) {
            resetOrder(oldOrder, order);
            throw new ErrorException(1053);
        }
        
        if(order.isAlreadyPaidAndDifferentStatus(oldOrder)) {
            logPrint("Tried to revert an order with a different payment status, incid: " + order.incrementOrderId);
            resetOrder(oldOrder, order);
            throw new ErrorException(1063);
        }
        
        if (order.needToStopDueToIllegalChangeInAddons(oldOrder, closedDate) && !order.forcedOpen) {
            resetOrder(oldOrder, order);
            throw new ErrorException(1053);
        }
        
        if (order.needToStopDueToIllegalChangePaymentDate(oldOrder, closedDate) && !order.forcedOpen && !order.isInvoice()) {
            resetOrder(oldOrder, order);
            throw new ErrorException(1053);
        }
        
        // If a order has created date that is not the same as the one in the database, what happend? 
        if (order.rowCreatedDate != null && oldOrder != null && !order.rowCreatedDate.equals(oldOrder.rowCreatedDate) && order.rowCreatedDate.before(closedDate) && !order.forcedOpen) {
            throw new ErrorException(1053);
        }
    }

    private void stopIfOverrideDateConflictingClosedDate(Order order, Date closedDate, Order oldOrder) throws ErrorException {
        
        // There are noe changes to the override accounting date. 
        if (oldOrder != null && oldOrder.overrideAccountingDate != null && oldOrder.overrideAccountingDate.equals(order.overrideAccountingDate)) {
            return;
        }
        
        // This prevents the user from moving an override accountingdate out of its locked periode.
        if (oldOrder != null && oldOrder.overrideAccountingDate != null && oldOrder.overrideAccountingDate.before(closedDate)) {
            if (!order.overrideAccountingDate.equals(oldOrder.overrideAccountingDate)) {
                resetOrder(oldOrder, order);
                throw new ErrorException(1053);    
            }
        }
        
        // Prevents order to be put into an closed periode
        if (order.overrideAccountingDate != null && order.overrideAccountingDate.before(closedDate)) {
            resetOrder(oldOrder, order);
            throw new ErrorException(1053);
        }
    }

    private void resetOrder(Order oldOrder, Order order) {
        if (oldOrder != null) {
            oldOrder = (Order)database.getObject(getCredentials(), oldOrder.id);
            orders.put(oldOrder.id, oldOrder);
        }
    }

    @Override
    public boolean isLocked(Date date) {
        Date closed = getOrderManagerSettings().closedTilPeriode;
        boolean isClosed = closed.after(date);
        return isClosed;
    }
    
    @Override
    public boolean isBankAccountClosed(Date date) {
        Date closed = getOrderManagerSettings().bankAccountClosedToDate;
        return closed.after(date);
    }

    @Override
    public PaymentTerminalInformation getTerminalInformation(String orderId) {
        Order order = getOrder(orderId);
        if (order != null) {
            return order.getTerminalInformation();
        }
        
        return null;
    }

    private void createAndSaveIncomeReport(Date start, Date end, List<DayIncome> days) {
        
        DayIncomeReport incomeReport = new DayIncomeReport();
        incomeReport.start = start;
        incomeReport.end = end;
        incomeReport.createdBy = getSession().currentUser.id;

        incomeReport.incomes = days;
        saveObject(incomeReport);
    }

    public Order getOrderDirect(String orderId) {
        return orders.get(orderId);
    }

    public boolean isCartWithinClosedPeriode(Cart cart) {
        Order order = new Order();
        order.cart = cart;
        
        Date closedDate = getOrderManagerSettings().closedTilPeriode;
        
        if (order.needToStopDueToIllegalChangeOfPriceMatrix(null, closedDate) && !order.forcedOpen) {
            return true;
        }
        
        if (order.needToStopDueToIllegalChangeInAddons(null, closedDate) && !order.forcedOpen) {
            return true;
        }
        
        if (order.needToStopDueToIllegalChangePaymentDate(null, closedDate) && !order.forcedOpen) {
            return true;
        }
        
        return false;
    }

    public void updateOrdersWithNewAccountingTaxCode(TaxGroup grp) {
        List<CartItem> cartItems = orders.values().stream()
                .flatMap(o -> o.getCartItems().stream())
                .filter(item -> item.getProduct().taxgroup == grp.groupNumber)
                .collect(Collectors.toList());
        
        cartItems.stream().forEach(item -> {
            item.getProduct().taxGroupObject.accountingTaxAccount = grp.accountingTaxAccount;
        });
        
        cartItems.stream()
                .map(item -> getOrder(item.orderId))
                .distinct()
                .forEach(order -> {
                    saveOrder(order);
                });
    }

    @Override
    public void checkGroupInvoicing(String password) {
        if (!password.equals("a9dsfasd23olnasdfnj2k4nj3jkasndf")) {
            return;
        }
        
        List<Order> ret = orders.values().stream()
                .filter(o -> o.isSamleFaktura())
                .collect(Collectors.toList());
        
        List<Order> creditNotes = ret.stream()
                .filter(o -> o.isCreditNote)
                .filter(o -> o.cart != null && o.cart.reference != null && o.cart.reference.equals("ordermanager_merged_order"))
                .collect(Collectors.toList());
        
        Map<String, List<Order>> groupedOrders = creditNotes.stream().collect(Collectors.groupingBy(Order::getParentOrder));
        
        for (String orderId : groupedOrders.keySet()) {
            if (groupedOrders.get(orderId).size() > 1) {

                boolean orderExists = isThereOrderCreatedBasedOnOrder(orderId);
                if (orderExists) {
                    logPrint("There are orders for the id: " + orderId + " but have multiple creditted orders? : orderid: " + getOrder(orderId).incrementOrderId);
                    groupedOrders.get(orderId).stream()
                        .forEach(i -> {
                            logPrint("  - Creditnoteid: " + i.incrementOrderId);
                        });
                }
                
                logPrint("Found a bit of a problem: " + groupedOrders.get(orderId).size());
                boolean first = false;
                List<Order> retOrders = groupedOrders.get(orderId);
                
                for (Order toD : retOrders) {
                    if (first && orderExists) {
                        first = false;
                        continue;
                    }   
                    
                    first = false;
                    orders.remove(toD.id);
                    deleteObject(toD);    
                    ordersChanged.add(toD.id);
                }
                
                Order origOrder = orders.get(orderId);
                origOrder.status = Order.Status.CREATED;
                origOrder.creditOrderId = new ArrayList();
                ordersChanged.add(origOrder.id);
                super.saveObject(origOrder); 
            }
        }
        
        List<Order> ordersWithNoRealInvoice = ret.stream()
                .filter(o -> o.status == Order.Status.PAYMENT_COMPLETED)
                .filter(o -> !isThereOrderCreatedBasedOnOrder(o.id))
                .collect(Collectors.toList());
            
        // Cleanup samlefaktura orders that does not have a real invoice.
        for (Order orderWithNoRealInvoice : ordersWithNoRealInvoice) {
            List<Order> creditNotesForOrder = getCreditNotesForOrder(orderWithNoRealInvoice.id);
            
            for (Order toD : creditNotesForOrder) {
                orders.remove(toD.id);
                deleteObject(toD);    
                ordersChanged.add(toD.id);
            }
            
            orderWithNoRealInvoice.status = Order.Status.CREATED;
            orderWithNoRealInvoice.creditOrderId = new ArrayList();
            ordersChanged.add(orderWithNoRealInvoice.id);
            super.saveObject(orderWithNoRealInvoice);
        }
    }
    
    public boolean isThereOrderCreatedBasedOnOrder(String orderId) {
        return orders.values().stream()
                .filter(order -> order.createdBasedOnOrderIds != null && order.createdBasedOnOrderIds.contains(orderId))
                .count() > 0;
    }

    public List<Order> getCreditNotesForOrder(String id) {
        return orders.values()
                .stream()
                .filter(o -> o.parentOrder != null && o.parentOrder.equals(id))
                .collect(Collectors.toList());
    }

    private long getNextIncrementalOrderId() {
        incrementingOrderId++;
        
        OrderManagerSettings settings = getOrderManagerSettings();
        if (settings.incrementalOrderId > incrementingOrderId) {
            return settings.incrementalOrderId;
        }
        
        return incrementingOrderId;
    }

    @Override
    public void setNewStartIncrementalOrderId(long incrementalOrderId, String password) {
        if (!password.equals("9adsf9023749haskdfh213847h7shafdiuhqw741hyiuher")) {
            return;
        }
        
        OrderManagerSettings settings = getOrderManagerSettings();
        settings.incrementalOrderId = incrementalOrderId;
        saveObject(settings);
    }

    @Override
    public List<Order> getOverdueInvoices(FilterOptions filterData) {
        List<Order> retOrders = orders.values()
                .stream()
                .filter(o -> o.isInvoice())
                .filter(o -> o.isOverdue())
                .collect(Collectors.toList());
        
        retOrders.forEach(o -> o.doFinalize());
        
        retOrders.sort((Order o1, Order o2) -> {
            return o1.dueDate.compareTo(o2.dueDate);
        });
        
        return retOrders;
        
    }

    @Override
    public Double getTotalOutstandingInvoices(FilterOptions filterData) {
        return orders.values()
                .stream()
                .filter(o -> o.isInvoice())
                .mapToDouble(o -> o.getPaidRest())
                .sum();
    }
    
    @Override
    public Double getTotalOutstandingInvoicesOverdue(FilterOptions filterData) {
        return getOverdueInvoices(filterData).stream()
                .mapToDouble(o -> getTotalAmount(o))
                .sum();
    }

    @Override
    public void changeProductOnCartItem(String orderId, String cartItemId, String productId) {
        Product product = productManager.getProduct(productId);
        Order order = getOrder(orderId);
        order.cart.getCartItem(cartItemId).setProduct(product);
        saveOrder(order);
    }

    @Override
    public void updateCartItemOnOrder(String orderId, CartItem cartItem) {
        Order order = getOrder(orderId);
        CartItem oldCartItem = order.cart.getCartItem(cartItem.getCartItemId());
        
        if (oldCartItem.getProduct() == null) {
            return;
        }
        
        oldCartItem.getProduct().name = cartItem.getProduct().name;
        oldCartItem.getProduct().description = cartItem.getProduct().description;
        
        if(oldCartItem.getProduct().taxgroup != cartItem.getProduct().taxgroup) {
            List<TaxGroup> taxgroups = productManager.getTaxes();
            for(TaxGroup grp : taxgroups) {
                if(grp.groupNumber == cartItem.getProduct().taxgroup) {
                    oldCartItem.getProduct().changeToTaxGroup(grp);
                }
            }
        }
        
        if (!oldCartItem.isPmsAddons() && !oldCartItem.isPriceMatrixItem()) {
            oldCartItem.setCount(cartItem.getCount());
            oldCartItem.getProduct().price = cartItem.getProduct().price;
        }
        
        saveOrder(order);
    }

    private boolean isInArray(DayIncome income, List<DayIncome> dayIncomes) {
        for (DayIncome i : dayIncomes) {
            if (i.start.equals(income.start) && i.end.equals(i.end)) {
                return true;
            }
        }
        
        return false;
    }

    public List<DayIncome> getDayIncomesIgnoreConfig(DayIncomeFilter filter) {
        filter.ignoreConfig = true;
        return getDayIncomesInternal(filter);
    }

    @Override
    public void addOrderTransaction(String orderId, double amount, String comment, Date paymentDate, Double amountInLocalCurrency, Double agio, String accountDetailId) {
        Order order = getOrder(orderId);
        if (order != null) {
            String userId = getSession().currentUser.id;
            order.registerTransaction(paymentDate, amount, userId, Order.OrderTransactionType.MANUAL, "", comment, amountInLocalCurrency, agio, accountDetailId);
            if (order.isFullyPaid()) {
                markAsPaidInternal(order, paymentDate, amount);
            }
            saveObject(order);
        }
    }
    
    public List<OrderTransactionDTO> getAllTransactionsForInvoices(Date start, Date end) {
        List<Order> invoices = getAllOrders().stream()
                .filter(o -> o.isInvoice())
                .collect(Collectors.toList());
        
        List<OrderTransactionDTO> retList = new ArrayList();
        
        for (Order order : invoices) {
            if (order.orderTransactions == null)
                continue;
            
            OrderLight orderLight = new OrderLight(order);
            
            for (OrderTransaction trans : order.orderTransactions) {
                boolean transactionWithinPeriode = (start.before(trans.date) && end.after(trans.date));
                
                if (start.equals(trans.date) || transactionWithinPeriode) {
                    OrderTransactionDTO dto = new OrderTransactionDTO();
                    dto.orderLight = orderLight;
                    dto.orderTransaction = trans;
                    retList.add(dto);
                }
                
            }
        }
        
        return retList;
    }

    @Override
    public void changeAutoClosePeriodesOnZRepport(boolean autoClose) {
        OrderManagerSettings settings = getOrderManagerSettings();
        settings.autoCloseFinancialDataWhenCreatingZReport = autoClose;
        saveObject(settings);
    }

    private void stopIfNewPaymentDatesConflictWithClosedPeriode(Order order, Date closedDate, Order oldOrder) {
        List<OrderTransaction> newTransactions = order.orderTransactions.stream()
                .filter(o -> !oldOrder.orderTransactions.contains(o))
                .collect(Collectors.toList());
        
        for (OrderTransaction transsaction : newTransactions) {
            if (transsaction.date.before(closedDate)) {
                resetOrder(oldOrder, order);
                throw new ErrorException(1053);
            }
        }
    }

    @Override
    public List<OrderUnsettledAmountForAccount> getOrdersUnsettledAmount(String accountNumber, Date endDate, String paymentId) {
        Date startDate = getStore().rowCreatedDate;
        List<DayIncome> dayEntries = new ArrayList();
        if (paymentId != null && !paymentId.isEmpty()) {
            dayEntries = getPaymentRecords(paymentId, startDate, endDate);
        } else {
            dayEntries = getDayIncomes(startDate, endDate);;
        }
        
        Map<String, List<DayEntry>> groupedEntries = dayEntries.stream()
                .flatMap(dayEntry -> dayEntry.dayEntries.stream())
                .filter(dayEntry -> dayEntry.accountingNumber.equals(accountNumber))
                .collect(Collectors.groupingBy(DayEntry::getOrderId));
     
        List<OrderUnsettledAmountForAccount> retList = new ArrayList();
        
        for (String orderId : groupedEntries.keySet()) {
            List<DayEntry> entries = groupedEntries.get(orderId);
            double sumOfAccount = entries.stream()
                    .mapToDouble(o -> o.amount.doubleValue())
                    .sum();
            
            if (sumOfAccount < 0.00001 && sumOfAccount > -0.00001) {
                continue;
            }
            
            OrderUnsettledAmountForAccount unsettledAmount = new OrderUnsettledAmountForAccount();
            unsettledAmount.amount = sumOfAccount;
            unsettledAmount.order = getOrder(orderId);
            unsettledAmount.account = accountNumber;
            
            if (unsettledAmount.amount < 1 && unsettledAmount.amount > -1) {
                continue;
            }
            
            retList.add(unsettledAmount);
        }
        
        return retList;
    }
     
    
    public List<PmiResult> getPmiResult(Date start, Date end) {
        ArrayList<PmiResult> result = new ArrayList();
        
        List<DayIncome> incomes = getDayIncomes(start, end);
        
        for (DayIncome dayIncome : incomes) {
            
            if (!dayIncome.isFinal && getOrderManagerSettings().autoCloseFinancialDataWhenCreatingZReport) {
                continue;
            }
            
            Map<String, List<DayEntry>> groupedByDepartmentId = dayIncome.dayEntries.stream()
                .filter(entry -> !(!entry.isActualIncome || entry.isOffsetRecord))
                .filter(o -> o.orderId != null)
                .collect(Collectors.groupingBy(o -> {
                    CartItem cartItem = getOrder(o.orderId).cart.getCartItem(o.cartItemId);
                    if (cartItem == null || cartItem.getProduct() == null || cartItem.getProduct().departmentId == null) {
                        return "";
                    }
                    
                    return cartItem.getProduct().departmentId;
                }));


            for (String departmentId : groupedByDepartmentId.keySet()) {
                Department department = departmentManager.getDepartment(departmentId);
                
                List<DayEntry> itemsWithDepartment = groupedByDepartmentId.get(departmentId);
                
                Map<String, List<DayEntry>> groupedByProductId = itemsWithDepartment
                        .stream()
                        .filter(o -> o.orderId != null)
                        .collect(Collectors.groupingBy(o -> o.accountingNumber));

                for (String accountingNumber : groupedByProductId.keySet()) {
                    int accountingCodeInt = -1;
                    
                    try {
                        accountingCodeInt = Integer.parseInt(accountingNumber);
                    } catch (Exception ex) {
                        // Ok.
                    }
                    
                    AccountingDetail detail = null;
                    if (accountingCodeInt > -1) {
                        detail = productManager.getAccountingDetail(accountingCodeInt);
                    }
                    
                    PmiResult toAdd = new PmiResult();
                    toAdd.department = department != null ? department.code : "";
                    toAdd.prodcutId = accountingNumber;
                    toAdd.propertyid = storeId;
                    toAdd.productName = detail != null ? accountingNumber + " " + detail.description : accountingNumber;
                    toAdd.revenue = groupedByProductId.get(accountingNumber).stream()
                            .mapToDouble(o -> o.amountExTax.doubleValue() * -1)
                            .sum();
                    toAdd.transactiondate = dayIncome.start;
                    
                    if (toAdd.revenue != 0) {
                        result.add(toAdd);
                    }
                }
            }
        }
        
        return result;
    }

    public void markAsTransferredToAccount(List<DayIncome> incomes) {
        BasicDBObject query = new BasicDBObject();
        for (DayIncome income : incomes) {
            query.put("incomes.id", income.id);
            List<DayIncomeReport> res = database.query("OrderManager", storeId, query).stream()
                    .map(o -> (DayIncomeReport)o)
                    .collect(Collectors.toList());
            
            for (DayIncomeReport rep : res) {
                for (DayIncome inc : rep.incomes) {
                    if (inc.id.equals(income.id)) {
                        DayIncomeTransferToAaccountingInformation info = new DayIncomeTransferToAaccountingInformation();
                        info.transferredByUserId = getSession().currentUser.id;
                        inc.accountingTransfer.add(info);
                    }
                }
                
                database.save("OrderManager", "col_"+storeId, rep);
            }            
        }
        
    }

    @Override
    public List<DayIncome> getPaymentRecords(String paymentId, Date from, Date to) {
        return getPaymentRecordsInternal(paymentId, from, to, true);
    }
    
    public List<DayIncome> getPaymentRecordsInternal(String paymentId, Date from, Date to, boolean doublePostingRecords) {
        boolean concatOcrPayments = paymentId.equals("70ace3f0-3981-11e3-aa6e-0800200c9a66") && storeOcrManager.isActivated();
        
        List<Order> orders = this.orders.values()
                .stream()
                .filter(o -> o.payment != null && o.payment.getPaymentTypeId().equals(paymentId))
                .collect(Collectors.toList());
        
        DayIncomeFilter filter = new DayIncomeFilter();
        if (doublePostingRecords) {
            filter.doublePostingRecords = true;
        } else {
            filter.onlyPaymentTransactionWhereDoubledPosting = true;
        }
        
        filter.includePaymentTransaction = false;
        filter.start = from;
        filter.end = to;
        
        OrderDailyBreaker breaker = new OrderDailyBreaker(orders, filter, paymentManager, productManager, getOrderManagerSettings().whatHourOfDayStartADay, getAllFreePosts(), storeOcrManager);
        breaker.breakOrders();
        
        return breaker.getDayIncomes();
    }

    @Override
    public void createNewDoubleTransferFile(String paymentId, Date from, Date to) {
        List<DayIncome> incomes = getPaymentRecordsInternal(paymentId, from, to, false);
        
        for (DayIncome inc : incomes) {
            inc.dayEntries.removeIf(o -> transactionIsTransferredToAccount(o.orderId, o.orderTransactionId));
            inc.dayEntries.removeIf(o -> o.accountingNumber == null || o.accountingNumber.equals("0000"));
        }
        
        incomes.removeIf(o -> o.dayEntries.isEmpty());
        
        if (incomes.isEmpty()) {
            return;
        }
        
        DoublePostAccountingTransfer accTransfer = new DoublePostAccountingTransfer();
        accTransfer.incomes = incomes;
        accTransfer.start = from;
        accTransfer.end = to;
        accTransfer.userId = getSession().currentUser.id;
        
        saveObject(accTransfer);
        
        incomes.stream()
            .flatMap(o -> o.dayEntries.stream())
            .forEach((Consumer<? super DayEntry>) o -> {
                Order order = getOrder(o.orderId);
                for (OrderTransaction transaction : order.orderTransactions) {
                    if (transaction.transactionId.equals(o.orderTransactionId)) {
                        transaction.transferredToAccounting = true;
                    }
                }
                saveObject(order);
            });
    }

    @Override
    public List<DoublePostAccountingTransfer> getAllDoublePostTransferFiles(String paymentId, Date from, Date to) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", DoublePostAccountingTransfer.class.getCanonicalName());
        
        return database.query("OrderManager", storeId, query)
                .stream()
                .map(o -> (DoublePostAccountingTransfer)o)
                .filter(o -> o.deleted == null)
                .filter(o -> o.isWithinOrEqual(from, to))
                .collect(Collectors.toList());
    }

    private boolean transactionIsTransferredToAccount(String orderId, String orderTransactionId) {
        if (orderId == null)
            return true;
        
        for (OrderTransaction transaction : getOrder(orderId).orderTransactions) {
            if (transaction.transactionId.equals(orderTransactionId) && transaction.transferredToAccounting) {
                return true;
            }
        }
                
        return false;
    }

    @Override
    public DoublePostAccountingTransfer getDoublePostAccountingTransfer(String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", DoublePostAccountingTransfer.class.getCanonicalName());
        query.put("_id", id);
        
        return database.query("OrderManager", storeId, query)
                .stream()
//                .filter(o -> o.deleted != null)
                .map(o -> (DoublePostAccountingTransfer)o)
                .findAny()
                .orElse(null);
    }

    /**
     * Will return the main invoice for a samlefaktura, but will
     * also return the creditnote for a main invoice if created.
     * 
     * @param id
     * @return 
     */
    public List<Order> getMainInvoices(String id) { 
        Order order = getOrderSecure(id);
        
        if (order == null) {
            return null;
        }
        
        return orders.values()
                .stream()
                .filter(o -> o.createdBasedOnOrderIds.contains(id) && !o.isCreditNote)
                .collect(Collectors.toList());
    }
    
    @Override
    public void readdTaxGroupToNullItems(String password) {
        if(!password.equals("!gfdsgdsf456&%__")) {
            return;
        }
        
        for(Order order : orders.values()) {
            boolean save = false;
            Payment defaultPaymentMethod = getStorePreferredPayementMethod();
            if (defaultPaymentMethod == null) {
                throw new RuntimeException("No default payment method set?");
            }
            
            if(order.payment == null) {
                order.payment = defaultPaymentMethod;
                save = true;
            }
            
            if(order.payment != null && (order.payment.paymentType == null || order.payment.paymentType.isEmpty())) {
                order.payment = defaultPaymentMethod;
                save = true;
            }
            
            if(order.cart != null) {
                for(CartItem item : order.getCartItems()) {
                    if(item.getProduct() != null && item.getProduct().taxGroupObject == null) {
                        item.getProduct().taxGroupObject = productManager.getProduct(item.getProductId()).taxGroupObject;
                        save = true;
                    }
                }
            }
            if(save) {
                saveOrder(order);
            }
        }
    }

    private void addOrdersToBookings(List<String> createdBasedOnOrderIds) {
        List<String> multiLevelNames = database.getMultilevelNames("PmsManager", storeId);
        
        createdBasedOnOrderIds.stream()
            .map(id -> getOrder(id))
            .forEach(order -> {
                List<String> roomIdsForOrder = order.getCartItems().stream()
                    .map(o -> o.getProduct().externalReferenceId)
                    .filter(o -> o != null)
                    .distinct()
                    .collect(Collectors.toList());

                for (String multilevelName : multiLevelNames) {
                    PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(multilevelName, PmsManager.class);
                    for (String pmsRoomId : roomIdsForOrder) {
                        PmsBooking booking = pmsManager.getBookingFromRoom(pmsRoomId);
                        if (booking == null) {
                            continue;
                        }
                        
                        if (!booking.orderIds.contains(order.id)) {
                            pmsManager.addOrderToBooking(booking, order.id);
                        }
                    }
                }
            });
    }
    
    private void addCreditNotesToBookings(List<String> createdBasedOnOrderIds) {
        
        List<String> multiLevelNames = database.getMultilevelNames("PmsManager", storeId);
        
        for (String multilevelName : multiLevelNames) {
            PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(multilevelName, PmsManager.class);
            
            createdBasedOnOrderIds.stream()
                    .forEach(orderId -> {
                        PmsBooking booking = pmsManager.getBookingWithOrderId(orderId);
                        
                        if (booking == null) {
                            return;
                        }
                        
                        Order order = getOrder(orderId);
                        
                        for (Order creditNote : getCreditNotesForOrder(orderId)) {
                            pmsManager.addOrderToBooking(booking, creditNote.id);
                        }
                    });
            
        }
    }

    @Override
    public boolean orderIsCredittedAndPaidFor(String orderId) {
        Order inOrder = getOrder(orderId);
        Order order = null;
        
        if (inOrder == null)
            return false;
        
        if (inOrder != null) {
            if (inOrder.parentOrder != null && !inOrder.parentOrder.isEmpty()) {
                order = getOrder(inOrder.parentOrder);
            } else {
                order = inOrder;
            }
        }
        
        if (order.status != Order.Status.PAYMENT_COMPLETED) {
            return false;
        }
        
        double sumOfPaidCreditNotes = getCreditNotesForOrder(order.id)
                .stream()
                .filter(o -> o.status == Order.Status.PAYMENT_COMPLETED)
                .mapToDouble(o -> o.getTotalAmount())
                .sum();
        
        double diff = sumOfPaidCreditNotes + order.getTotalAmount();
        
        return diff < 0.0001 && diff > -0.0001;
    }

    @Override
    public List<String> filterOrdersIsCredittedAndPaidFor(List<String> orderIds) {
        return orderIds.stream()
                .filter(id -> !orderIsCredittedAndPaidFor(id))
                .collect(Collectors.toList());
    }

    public void autoMarkAsPaid(List<String> orderIds) {
        orderIds.stream()
            .map(id -> orders.get(id))
            .filter(order -> !order.hasTriedToAutoCollect)
            .filter(order -> order != null && order.payment.paymentType != null && order.status != Order.Status.PAYMENT_COMPLETED)
            .forEach(order -> {
                order.hasTriedToAutoCollect = true;
                try {
                    Application paymentapp = storeApplicationPool.getApplicationByNameSpace(order.payment.paymentType);
                    if(paymentapp != null) {
                        String automarkpaid = paymentapp.getSetting("automarkpaid");
                        Date date = order.getEndDateByItems();
                        
                        if (date == null) {
                            date = new Date();
                        }
                        
                        if(automarkpaid != null && automarkpaid.equals("true")) {
                            markAsPaid(order.id, date, getTotalAmount(order));
                        }
                    }
                }catch(Exception e) {
//                    messageManager.sendErrorNotification("Failed to automark order as paid", e);
                }
                saveObject(order);
            });  
    }

    @Override
    public void forceSetNewPaymentDate(String orderId, Date date, String password) {
        if (password == null || !password.equals("fdsvb4354345345")) {
            return;
        }
        
        Order order = orders.get(orderId);
        if (order != null) {
            order.paymentDate = date;
            super.saveObject(order);
        }
    }

    public void generateKid(Order order) {
        boolean kidExists = order.kid != null && !order.kid.isEmpty();
        
        if (kidExists || !order.isInvoice()) {
            return;
        }
        
        AccountingDetails details = invoiceManager.getAccountingDetails();
        if(details.kidType != null && !details.kidType.isEmpty()) {
            if(details.kidType.equals("orderid") && order.incrementOrderId > 0) {
                order.generateKidLuhn(order.incrementOrderId + "", details.kidSize);
            } else if(details.kidType.equals("customerid")) {
                User user = userManager.getUserById(order.userId);
                order.generateKidLuhn(user.customerId + "", details.kidSize);
            } else if(details.kidType.equals("customeridandorderid")) {
                User user = userManager.getUserById(order.userId);
                order.generateKidLuhn(user.customerId + "" + order.incrementOrderId, details.kidSize);
            }
            saveOrder(order);
        }
    }

    @Override
    public AccountingBalance getBalance(Date date, String paymentId) {
        AccountingBalance balance = new AccountingBalance();
        balance.balanceToDate = date;
        
        List<DayIncome> res = new ArrayList();
        
        if (paymentId == null || paymentId.isEmpty()) {
            res = getDayIncomes(getStore().rowCreatedDate, date);
        } else {
            res = getPaymentRecords(paymentId, getStore().rowCreatedDate, date);
        }
        
        addBalance(res, balance);
        
        return balance;
    }

    private void addBalance(List<DayIncome> res, AccountingBalance balance) {
        Map<String, List<DayEntry>> groupedByAccountNumber = res.stream()
                .flatMap(o -> o.dayEntries.stream())
                .collect(Collectors.groupingBy(o -> o.accountingNumber));
        
        for (String accountNumber : groupedByAccountNumber.keySet()) {
            Double total = groupedByAccountNumber.get(accountNumber).stream()
                    .mapToDouble(o -> o.amount.doubleValue())
                    .sum();
            
            balance.balances.put(accountNumber, total);
        }
    }

    @Override
    public List<DayIncome> getDayIncomesWithMetaData(Date start, Date end) {
        List<DayIncome> incomes = getDayIncomes(start, end);
        
        incomes.stream()
                .flatMap(o -> o.dayEntries.stream())
                .filter(o -> o.orderId != null)
                .forEach(dayIncome -> {
                    addMetaDataToDayIncome(dayIncome);
                });
        
        return incomes;
    }

    private void addMetaDataToDayIncome(DayEntry dayIncome) {
        Order order = getOrder(dayIncome.orderId);
        CartItem item = order.cart.getCartItem(dayIncome.cartItemId);
        
        if (item != null) {
            if (item.isPmsAddons()) {
                dayIncome.metaData.put("Guest name", item.getProduct().metaData);
            } else if (item.isPriceMatrixItem()) {
                dayIncome.metaData.put("Guest name", item.getProduct().metaData);
            } else {
                dayIncome.metaData.put("Guest name", "N/A");
            }
        } else {
            if (order.cart.address != null && order.cart.address.fullName != null) {
                dayIncome.metaData.put("Guest name", order.cart.address.fullName);
            } else {
                User user = userManager.getUserById(order.id);
                if (user != null) {
                    dayIncome.metaData.put("Guest name", user.fullName);
                } else {
                    dayIncome.metaData.put("Guest name", "N/A");
                }
            }   
        }        
    }

    @Override
    public List<DayIncome> getDoublePostingDayIncomes(String paymentId, Date start, Date end) {
        
        List<DayIncome> res = getDayIncomes(start, end);
        res.addAll(getPaymentRecords(paymentId, start, end));
        
        return res;
    }    
    
    public Map<String, List<String>> getOrdersGroupedByExternalReferenceId() {
        
        Map<String, List<String>> retMap = new HashMap();
        
        for (Order order : orders.values()) {
            for (CartItem cartItem : order.getCartItems()) {
                if (cartItem.getProduct() != null && cartItem.getProduct().externalReferenceId != null && !cartItem.getProduct().externalReferenceId.isEmpty()) {
                    List<String> externalRefIds = retMap.get(order.id);
                    if (externalRefIds == null) {
                        externalRefIds = new ArrayList();
                        retMap.put(order.id, externalRefIds);
                    }
                    
                    externalRefIds.add(cartItem.getProduct().externalReferenceId);
                }
            }
        }
        
        return retMap;
    }

    private void addOrderToBooking(Order order) {
        List<String> ids = new ArrayList();
        ids.add(order.id);
        addOrdersToBookings(ids);
    }

    @Override
    public void chargeOrder(String orderId, String tokenId) {
        Double amount = getTotalForOrderById(orderId);
        orderToPay = getOrderSecure(orderId);
        tokenInUse = tokenId;
        
        orderToPay.payment.paymentType = "ns_8edb700e_b486_47ac_a05f_c61967a734b1\\IntegratedPaymentTerminal";
        saveOrder(orderToPay);
        
        GdsPaymentAction paymentAction = new GdsPaymentAction();
        paymentAction.amount = (int)(amount * 100);
        paymentAction.action = GdsPaymentAction.Actions.STARTPAYMENT;
        GetShopDevice device = gdsManager.getDeviceByToken(tokenId);
        gdsManager.sendMessageToDevice(device.id, paymentAction);
        printFeedBack("Starting payment process");
    }
    
    @Override
    public List<String> getTerminalMessages() {
        
        for (String msg : terminalMessages) {
            if (msg != null && msg.equals("completed") && orderToPay != null && orderToPay.status != Order.Status.PAYMENT_COMPLETED) {
                markOrderInProgressAsPaid();
            }
        }
        
        return terminalMessages;
    }
    
    public void markOrderInProgressAsPaid() {
        double paidAmount = orderToPay.getTotalAmount() + orderToPay.cashWithdrawal;
        markAsPaid(orderToPay.id, new Date(), paidAmount);
    }

    @Override
    public Boolean isPaymentInProgress() {
        return orderToPay != null;
    }
    
    private void printFeedBack(String string) {
        VerifoneFeedback feedBack = new VerifoneFeedback();
        feedBack.msg = string;
        terminalMessages.add(string);
        if(orderToPay != null && orderToPay.payment != null) {
            orderToPay.payment.transactionLog.put(System.currentTimeMillis(), string);
        }
        logPrint("\t" + string);
    }

    @Override
    public void paymentResponse(String tokenId, TerminalResponse response) {
        if(tokenInUse == null || !tokenId.equals(tokenInUse)) {
            return;
        }
        Gson gson = new Gson();
        
        if(response.paymentSuccess()) {
            terminalMessages.add("completed");
            markOrderInProgressAsPaid();
        } else {
            terminalMessages.add("payment failed");
        }
        
        orderToPay.terminalResponses.put(new Date().getTime(), response);
        saveOrder(orderToPay);
        orderToPay = null;
    }

    @Override
    public void paymentText(String tokenId, String text) {
        if(tokenInUse == null || !tokenId.equals(tokenInUse)) {
            return;
        }
        
        terminalMessages.add(text);
    }

    @Override
    public void clearMessages() {
        terminalMessages.clear();
    }

    public void removeOrderToPay() {
        orderToPay = null;
    }

    @Override
    public boolean hasNoOrders() {
        return orders.keySet().isEmpty();
    }

    private void updateCurrencyForItems(Order order) {
        if (order.currency == null || order.currency.isEmpty() || order.isCreditNote) {
            return;
        }
        
        Order oldOrder = null;
        
        if (order.id != null && !order.id.isEmpty()) {
            oldOrder = (Order)database.getObject(getCredentials(), order.id);
        }
        
        for (CartItem item : order.getCartItems()) {
            boolean samePriceInTaxAsOldOrder = oldOrder != null && oldOrder.cart.getCartItem(item.getCartItemId()) != null && oldOrder.cart.getCartItem(item.getCartItemId()).getProductPrice() == item.getProductPrice();
            boolean currencySame = oldOrder != null && order.currency.equals(oldOrder.currency);
            boolean hasOldOrder = oldOrder != null;
            
            if (item.getProduct().priceLocalCurrency == null || !samePriceInTaxAsOldOrder || !currencySame || !hasOldOrder) {
                item.getProduct().priceLocalCurrency = convertCurrency(order, item.getProduct().price);
                logPrint("Calculating the order prices to: " + item.getProduct().priceLocalCurrency);
            } else {
                logPrint("Skipping calcualation, already set " + item.getProduct().priceLocalCurrency + " and same price as before");
            }
            
            
        }
    }

    public String getLocalCurrencyCode() {
        String localCurrency = getStoreSettingsApplication().getSetting("currencycode");
        
        if (localCurrency == null || localCurrency.isEmpty()) {
            localCurrency = "NOK";
        }
        
        return localCurrency;
    }
    
    private Double convertCurrency(Order order, double price) {
        String localCurrency = getLocalCurrencyCode();
        
        String covertString = order.currency+"_"+localCurrency;
        covertString = covertString.toUpperCase();
        
        JsonObject res;
        
        try {
            String url = "https://free.currconv.com/api/v7/convert?q="+covertString+"&compact=ultra&apiKey=a937737cc8a3af4b1766";
            logPrint("Using url to fetch currency convertion: " + url);
            res = webManager.htmlGetJson(url);
        } catch (Exception ex) {
            logPrintException(ex);
            return null;
        }
        
        if (res != null) {
            double convertNumber = res.get(covertString).getAsDouble();
            return price * convertNumber;
        } else {
            logPrint("Warning, the currency converter returned a null response");
        }
        
        return null;
    }

    private void closeSegmentsInBookingManager() {
        List<String> multiLevelNames = database.getMultilevelNames("PmsManager", storeId);
        
        for (String multilevelName : multiLevelNames) {
            PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(multilevelName, PmsManager.class);
            pmsManager.closeSegmentsForBookings();
        }
                    
    }

    @Override
    public AccountingFreePost saveFreePost(AccountingFreePost freePost) {
        validateFreePost(freePost);
        
        if (freePost.createdByUserId == null) {
            freePost.createdByUserId = getSession().currentUser.id;
        }
        
        saveObject(freePost);
        accountingFreePosts.put(freePost.id, freePost);
        return freePost;
    }

    @Override
    public AccountingFreePost getAccountFreePost(String id) {
        return accountingFreePosts.get(id);
    }

    @Override
    public void deleteFreePost(String id) {
        AccountingFreePost freePost = accountingFreePosts.get(id);
        if (freePost == null)
            return;
        
        if (freePost.closed) {
            throw new ErrorException(1061);
        }
        
        accountingFreePosts.remove(id);
        deleteObject(freePost);
    }

    private void validateFreePost(AccountingFreePost freePost) {
        if (freePost.closed) {
            throw new ErrorException(1061);
        }
        
        if (freePost.id != null && !freePost.id.isEmpty()) {
            AccountingFreePost oldPost = (AccountingFreePost) database.findObject(freePost.id, "OrderManager");
            
            if (oldPost != null && oldPost.closed) {
                throw new ErrorException(1062);
            }
            
        }
        
        if (freePost.date == null) {
            throw new ErrorException(1062);
        }
        
        if (freePost.date.before(getOrderManagerSettings().closedTilPeriode)) {
            throw new ErrorException(1062);
        }
    }

    private List<AccountingFreePost> getAllFreePosts() {
        return new ArrayList(accountingFreePosts.values());
    }

    private void markFreePostingAsClosed(String freePostId) {
        AccountingFreePost freePost = getAccountFreePost(freePostId);
        if (freePost != null) {
            freePost.closed = true;
            saveObject(freePost);
        }
    }


    private void resetFreePostsEntries(DataCommon dataObject) {
        DayIncomeReport income = (DayIncomeReport)dataObject;
        
        income.incomes.stream()
                .flatMap(o -> o.dayEntries.stream())
                .filter(o -> o.freePostId != null && !o.freePostId.isEmpty())
                .map(o -> getAccountFreePost(o.freePostId))
                .filter(o -> o != null)
                .forEach(o -> {
                    o.closed = false;
                    saveObject(o);
                });
    }

    @Override
    public List<DiffReport> getDiffReport(Date start, Date end, boolean incTaxes) {
        List<DayIncome> lockedReport = getDayIncomes(start, end);
        List<DayIncome> currentReport = getCurrentDayIncomes(start, end);
        
        DiffReportCreator creator = new DiffReportCreator();
        return creator.createReport(lockedReport, currentReport, incTaxes);
    }
    
    @Override
    public void applyCorrectionForOrder(String orderId, String password) {
        if (password == null || !password.equals("asdfjadsfjasdkflj")) {
            return;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 10);
        
        DayIncomeFilter filter = new DayIncomeFilter();
        filter.start = new Date(0);
        filter.end = cal.getTime();
        
        ArrayList<Order> ordersToBreak = new ArrayList();
        ordersToBreak.add(orders.get(orderId));
        OrderDailyBreaker breaker = new OrderDailyBreaker(ordersToBreak, filter, paymentManager, productManager, 0, new ArrayList(), storeOcrManager);
        breaker.breakOrders();
        List<DayIncome> currentDayIncomes = breaker.getDayIncomes();
        
        BasicDBObject query = new BasicDBObject();
        query.put("className", DayIncomeReport.class.getCanonicalName());
        
        database.query("OrderManager", storeId, query)
                .stream()
                .map(o -> (DayIncomeReport)o)
                .forEach(o -> {
                    boolean found = false;
                    
                    for (DayIncome dayIncome : o.incomes) {
                        List<DayEntry> dayEntriesToRemove = new ArrayList();
                        
                        for (DayEntry entry : dayIncome.dayEntries) {
                            if (entry.orderId != null && entry.orderId.equals(orderId)) {
                                dayEntriesToRemove.add(entry);
                            }
                        }
                        
                        DayIncome dayIncomeToAdd = null;
                        
                        for (DayIncome currentIncome : currentDayIncomes) {
                            if (currentIncome.start.equals(dayIncome.start)) {
                                dayIncomeToAdd = currentIncome;
                                break;
                            }
                        }
                        
                        if (dayIncomeToAdd != null) {
                            List<DayEntry> dayEntriesToAdd = dayIncomeToAdd
                                    .dayEntries
                                    .stream()
                                    .filter(j -> j.orderId != null && j.orderId.equals(orderId))
                                    .collect(Collectors.toList());
                            
                            dayIncome.dayEntries.removeAll(dayEntriesToRemove);
                            dayIncome.dayEntries.addAll(dayEntriesToAdd);
                            
                            if (!dayEntriesToRemove.isEmpty() || !dayEntriesToAdd.isEmpty()) {
                                found = true;    
                            }
                            
                        }   
                    }
                    
                    if (found) {
                        saveObject(o);
                    }
                });
        
    }

    private List<DayIncome> getCurrentDayIncomes(Date start, Date end) {
        DayIncomeFilter filter = new DayIncomeFilter();
        filter.start = start;
        filter.end = end;
        filter.ignoreConfig = false;
        filter.ignoreFromDatabase = true;
        List<DayIncome> currentReport = getDayIncomesInternal(filter);
        return currentReport;
    }

    @Override
    public void forceChangeOverrideAccountingDate(String password, String orderId, Date overrideDate) {
        
        if (password == null || !password.equals("omfg_fml_9adufs9q342h5lkadsfaksdfh243asdfjhasdfhaskdfhj234")) {
            return;
        }
        
        Order order = getOrder(orderId);
        if (order == null)
            return;
        
        order.overrideAccountingDate = overrideDate;
        saveObjectDirect(order);
    }

    @Override
    public List<Order> getAutoCreatedOrdersForConference(String conferenceId) {
        return orders.values()
                .stream()
                .filter(o -> o.autoCreatedOrderForConferenceId != null && o.autoCreatedOrderForConferenceId.contains(conferenceId))
                .collect(Collectors.toList());
    }

    public List<Order> getAllOrdersNotClosed() { 
        List<Order> orderList = orders.values()
                .stream()
                .filter(o -> !o.closed)
                .collect(Collectors.toList());
        
        return orderList;
    }
    
    public Order getOrderCreatedByPaymentLinkWithRoomId(String roomBookingId) {
        return orders.values()
                .stream()
                .filter(o -> o.createdByPaymentLinkId != null && o.createdByPaymentLinkId.equals(roomBookingId))
                .filter(o -> !o.isFullyPaid() || o.status != Order.Status.PAYMENT_COMPLETED)
                .filter(o -> !o.isCreditNote)
                .filter(o -> !o.isNullOrder())
                .findFirst()
                .orElse(null);
    } 

    @Override
    public List<String> getAllOrdersForRoom(String pmsBookingRoomId) {
        return orders.values()
                .stream()
                .filter(o -> o.containsRoom(pmsBookingRoomId))
                .map(o -> o.id)
                .collect(Collectors.toList());
    }

    private void updateStock(Order order) {
        if (!isStockManagementActive()) {
            return;
        }
        
        wareHouseManager.updateStockQuantity(order);
    }

    /**
     * We should find some way to each money on this feature as
     * there are much more data stored for each order etc.
     * 
     * @return 
     */
    @Override
    public boolean isStockManagementActive() {
        return storeId.equals("13442b34-31e5-424c-bb23-a396b7aeb8ca");
    }

    private void cleanupEmptyAddonIds() {
        List<Order> incrementalOrderIds = new ArrayList();            
        
        for (Order order : orders.values()) {
            if (order != null) {
                for (CartItem item : order.getCartItems()) {
                    if (item.itemsAdded != null && !item.itemsAdded.isEmpty()) {
                        for (PmsBookingAddonItem aitem : item.itemsAdded) {
                            if (aitem.addonId == null || aitem.addonId.isEmpty()) {
                                aitem.addonId = UUID.randomUUID().toString();
                                if (!incrementalOrderIds.contains(order.incrementOrderId)) {
                                    incrementalOrderIds.add(order);
                                }
                            }
                        }
                    }

                }
            }
        }

        incrementalOrderIds.stream().forEach(o -> super.saveObject(o));
    }
    
    @Override
    public void resetLanguageAndCurrencyOnOrders() {
        String currency = getLocalCurrencyCode();
        String language = getSession().language;
        
        for(Order ord : orders.values()) {
            if(ord.incrementOrderId == 100081) {
                System.out.println("okey");
            }
            ord.language = language;
            ord.currency = currency;
            super.saveObject(ord);
        }
    }

    @Override
    public Order changeUserOnOrder(String orderId, String userId) {
        Order order = getOrder(orderId);
        order.userId = userId;
        finalizeOrder(order);
        saveOrder(order);
        return order;
    }

    @Override
    public void doRoundUpOnCurrentOrder(String orderId) {
        Order order = getOrderSecure(orderId);
        String currency = storeManager.getCurrency();
        String language = StoreManager.getLanguage();
        
        Product prod = productManager.getProduct("roundupproduct");
        if(prod == null) {
            String productName = "Øreavrunding";
            if(!language.equals("no")) {
                productName = "Exchange roundup";
            }
            
            prod = new Product();
            prod.name = productName;
            prod.id = "roundupproduct";
            prod.price = 0.0;
            prod.taxgroup = 0;
            productManager.saveProduct(prod);
        }
        
        List<String> removeItem = new ArrayList();
        for(CartItem item : order.getCartItems()) {
            if(item.getProduct().id.equals("roundupproduct")) {
                removeItem.add(item.getCartItemId());
            }
        }
        for(String itemId : removeItem) {
            order.cart.removeItem(itemId);
        }
        
        saveOrder(order);
        
        double total = getTotalAmount(order);
        double ceiltotal = Math.ceil(total);
        double roundup = ceiltotal - total;
        
        if(roundup > 0.1) {
            
            if(currency.equals("adifferentcurrency")) {
                //Do something here.
            } else {
                roundup = Math.round(roundup*100) / (double)100;
            }
            addProductToOrder(order.id, prod.id, 1);
            order = getOrder(orderId);
            for(CartItem item : order.getCartItems()) {
                if(item.getProduct().id.equals("roundupproduct")) {
                    item.getProduct().price = roundup;
                }
            }
            saveOrder(order);
        }
    }

    @Override
    public void deleteDoublePostingFile(String fileId) {
        DoublePostAccountingTransfer file = getDoublePostAccountingTransfer(fileId);
        if (file == null) {
            return;
        }
        
        for (DayIncome income : file.incomes) {
            for (DayEntry entry : income.dayEntries) {
                for (Order order : orders.values()) {
                    boolean found = false;
                    for (OrderTransaction trans : order.orderTransactions) {
                        if (trans.transactionId.equals(entry.orderTransactionId)) {
                            trans.transferredToAccounting = false;
                            found = true;
                        }
                    }
                    if (found) {
                        saveObject(order);
                    }
                }
            }
        }
       
        deleteObject(file);
    }

    @Override
    public void cleanupMessedUpOrderTransactionForForignCurrencyCreditNotes(String password) {
        if (!password.equals("asd9fasdfiasdjfoasidfjqaweraisdfnaejdfn")) {
            return;
        }
        
        List<Order> fixOrders = orders
            .values()
            .stream()
            .filter(order -> {

                if (order.currency != null && !order.currency.isEmpty()) {
                    for (OrderTransaction trans : order.orderTransactions) {
                        if (trans.amountInLocalCurrency == null) {
                            return true;
                        }
                    }
                }

                return false;
            })
            .collect(Collectors.toList());

        for (Order order : fixOrders) {
            for (OrderTransaction trans : order.orderTransactions) {
                if (trans.amountInLocalCurrency == null) {
                    trans.amountInLocalCurrency = 0D;
                }
            }
            
            saveObject(order);
        }
    }

    @Override
    public List<DayEntry> getActualDayIncome(Date start, Date end) {
        List<DayIncome> income = getDayIncomes(start, end);
        List<DayEntry> res = new ArrayList();
        for(DayIncome in : income) {
            for(DayEntry entry : in.dayEntries) {
                if(entry.isActualIncome && !entry.isOffsetRecord) {
                    res.add(entry);
                }
            }
        }
        return res;
    }

}