package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.thundashop.core.accountingmanager.AccountingSystemStatistics;
import com.thundashop.core.accountingmanager.AccountingSystemStatisticsResult;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.Coupon;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshopaccounting.GBat10AccountingSystem;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderShipmentLogEntry;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.ordermanager.data.VirtualOrder;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.sendregning.SendRegningManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsInvoiceManager extends GetShopSessionBeanNamed implements IPmsInvoiceManager {

    private boolean avoidChangeInvoicedTo;
    private boolean avoidChangingInvoicedFrom;
    private List<String> roomIdsInCart = null;
    private HashMap<String, PmsAdvancePriceYield> advancePriceYields = new HashMap();
    private HashMap<String, PmsOrderStatsFilter> savedIncomeFilters = new HashMap();
    private HashMap<Long, Integer> savedCoverage = new HashMap();
    private PmsPaymentLinksConfiguration paymentLinkConfig = new PmsPaymentLinksConfiguration();
    private Date cacheCoverage = null;
    
    @Autowired
    PmsNotificationManager pmsNotificationManager;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    @Autowired
    SendRegningManager sendRegningManager;
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    GBat10AccountingSystem gBat10AccountingSystem;
    
    private Double getAddonsPriceIncludedInRoom(PmsBookingRooms room, Date startDate, Date endDate) {
        double res = 0.0;
        for(PmsBookingAddonItem item : room.addons) {
            if((item.date.after(startDate) || room.isSameDay(item.date, startDate)) && item.date.before(endDate)) {
                if(item.isIncludedInRoomPrice) {
                    res += (item.price * item.count);
                }
            }
        }
        return res;
    }

    private void clearCartExcept(List<String> itemsToCreate) {
        List<CartItem> added = new ArrayList();
        for(String id : itemsToCreate) {
            CartItem item = cartManager.getCart().getCartItem(id);
            added.add(item);
        }
        cartManager.clear();
        cartManager.getCart().addCartItems(added);
    }

    @Override
    public List<Order> fetchDibsOrdersToAutoPay() {
        return new ArrayList();
    }
    
    private boolean sameMonth(Date startDate, Date endDate) {
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal.setTime(startDate);
        cal2.setTime(endDate);
        
        if(cal.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }

    @Override
    public String createOrderOnUnsettledAmount(String bookingId) {
        NewOrderFilter filter = new NewOrderFilter();
        PmsBooking booking = pmsManager.getBooking(bookingId);
        filter.endInvoiceAt = booking.getEndDate();
        filter.prepayment = true;
        return createOrder(bookingId, filter);
    }

    private List<CartItem> getLostItems(PmsBooking booking) {
        List<String> rooms = new ArrayList();
        for(PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            rooms.add(room.pmsBookingRoomId);
        }
        
        List<CartItem> lostRooms = new ArrayList();

        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            for(CartItem item : order.cart.getItems()) {
                if(item.removedAfterDeleted) {
                    continue;
                }
                if(item.getProduct().externalReferenceId != null && !rooms.contains(item.getProduct().externalReferenceId)) {
                    if(!avoidOrderCreation) {
                        item.removedAfterDeleted = true;
                    }
                    lostRooms.add(item);
                    CartItem copy = item.copy();
                    copy.setCount(item.getCount() * -1);
                    itemsToReturn.add(copy);
                }
            }
        }
        
        return lostRooms;
    }

    @Override
    public PmsPaymentLinksConfiguration getPaymentLinkConfig() {
        return paymentLinkConfig;
    }

    @Override
    public void savePaymentLinkConfig(PmsPaymentLinksConfiguration config) {
        if(paymentLinkConfig.id != null && !paymentLinkConfig.id.isEmpty()) {
            if(!paymentLinkConfig.id.equals(config.id)) {
                Exception ex = new Exception("Incorrect id when saving paymentlink config");
                logPrint(ex);
                return;
            }
        }
        
        saveObject(config);
        paymentLinkConfig = config;
    }


    private Payment getOverriddenPaymentType(NewOrderFilter filter) {
        Application paymentApplication = applicationPool.getApplication(filter.paymentType);
        Payment payment = new Payment();
        payment.paymentType = "ns_" + paymentApplication.id.replace("-", "_") + "\\" + paymentApplication.appName;
        payment.paymentId = paymentApplication.id;
        return payment;

    }

    private String createOrderOld(String bookingId, NewOrderFilter filter) {

        
        avoidChangeInvoicedTo = false;
        avoidChangingInvoicedFrom = false;
        runningDiffRoutine = false;
        if(filter.itemsToCreate.isEmpty()) {
            itemsToReturn.clear();
        }
        this.avoidOrderCreation = filter.avoidOrderCreation;
        
        List<PmsBooking> allbookings = new ArrayList();
        if(bookingId == null) {
            List<PmsBooking> tocheck = pmsManager.getAllBookings(null);
            for(PmsBooking book : tocheck) {
                if(book.isEndedOverTwoMonthsAgo() || !shouldBeProcessed(book)) {
                    continue;
                }
                allbookings.add(book);
            }
        } else {
            PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
            if(booking != null) {
                allbookings.add(booking);
            }
        }
        
        String lastOrderId = "";
        for(PmsBooking booking : allbookings) {
            if(filter.itemsToCreate.isEmpty()) {
                clearCart();
                addBookingToCart(booking, filter);
            } else {
                clearCartExcept(filter.itemsToCreate);
            }
            if(!itemsToReturn.isEmpty() || !filter.itemsToCreate.isEmpty()) {
                if(avoidOrderCreation) {
                    continue;
                }
                Order order = null;
                order = getUnpaidOrder(booking);
                if(filter.addToOrderId != null && !filter.addToOrderId.isEmpty()) {
                    order = orderManager.getOrderSecure(filter.addToOrderId);
                    if(order.closed) {
                        order = null;
                    }
                } else if(filter.createNewOrder) {
                    order = null;
                }
                if(order != null) {
                    order.cart.addCartItems(itemsToReturn);
                    if(filter.paymentType != null && !filter.paymentType.isEmpty()) {
                        order.payment = getOverriddenPaymentType(filter);
                    }
                    orderManager.saveOrder(order);
                } else {
                    if(filter.itemsToCreate.isEmpty()) {
                        updateCart();
                    }
                    order = createOrderFromCart(booking, filter, false);
                    if (order == null) {
                        return "Could not create order.";
                    }
                    order.dueDays = booking.dueDays;
                    if(filter.userId != null && !filter.userId.isEmpty()) {
                        order.userId = filter.userId;
                        orderManager.saveOrder(order);
                    }
                    autoSendInvoice(order, booking.id);
                    pmsManager.addOrderToBooking(booking, order.id);
                    Double total = orderManager.getTotalAmount(order);
                    if(total < 0) {
                        List<String> emails = pmsManager.getConfigurationSecure().emailsToNotify.get("creditorder");
                        String message = "Order " + order.incrementOrderId + " has been credited from external channel, total amount: " + total + "<br><br>";
                        try { 
                           User user = userManager.getUserById(order.userId);
                            message += "Name: " + user.fullName + ", " + user.emailAddress + ", " + "(" + user.prefix + ") " + user.cellPhone + "<br>";
                            message += "Other orders connected to booking:<br>";
                            for(String orderId : booking.orderIds) {
                                Order otherOrder = orderManager.getOrderSecure(orderId);
                                message += otherOrder.incrementOrderId + " (" + orderManager.getTotalAmount(otherOrder) + ")<br>";
                            }
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                        if(emails != null) {
                            for(String email : emails) {
                                messageManager.sendMail(email, email, "Credited order", message, email, email);
                            }
                        }
                    }
                    Double amount = orderManager.getTotalAmount(order);
                    if(filter.addToOrderId != null && !filter.addToOrderId.isEmpty() && amount < 0) {
                        Order baseOrder = orderManager.getOrderSecure(filter.addToOrderId);
                        baseOrder.creditOrderId.add(order.id);
                        order.payment = baseOrder.payment;
                        orderManager.saveOrder(order);
                    }
                }
                
                for(PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
                    if(filter.pmsRoomId != null && !filter.pmsRoomId.isEmpty()) {
                        if(!room.pmsBookingRoomId.equals(filter.pmsRoomId)) {
                            continue;
                        }
                    }
                    if(roomIdsInCart.contains(room.pmsBookingRoomId)) {
                        room.invoicedFrom = room.date.start;
                        if(filter.endInvoiceAt != null && room.date.end.before(filter.endInvoiceAt) && !pmsManager.getConfigurationSecure().ignoreRoomToEndDate) {
                            room.invoicedTo = room.date.end;
                        } else {
                            room.invoicedTo = filter.endInvoiceAt;
                        }
                    }
                }
                lastOrderId = order.id;
                boolean saveOrder = false;
                if(filter.pmsRoomId != null && !filter.pmsRoomId.isEmpty()) {
                    order.attachedToRoom = filter.pmsRoomId;
                    saveOrder = true;
                }
                
                if(filter.chargeCardAfter != null) {
                    order.chargeAfterDate = filter.chargeCardAfter;
                    saveOrder = true;
                }
                if(saveOrder) {
                    orderManager.saveOrder(order);
                }
                
                pmsManager.saveBooking(booking);
                cartManager.clear();
            }
        }
        
        if(lastOrderId.isEmpty() && itemsToReturn.isEmpty() && filter.addToOrderId != null && !filter.addToOrderId.isEmpty()) {
            lastOrderId = filter.addToOrderId;
        }
        
        if(lastOrderId.isEmpty() && !avoidOrderCreation) {
            logPrint("Returning an empty order id, this is wrong.");
        }
        if(filter.addToOrderId.isEmpty()) {
            updateCart();
        }
        return lastOrderId;
    }
    
    @Override
    public boolean supportsDailyPmsInvoiceing(String bookingId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        if(booking.priceType != PmsBooking.PriceType.daily) {
            return false;
        }
        String plugin = findPricePluginForBooking(booking);
        
        if(!plugin.equals("pmsdailyordergeneration")) {
            return false;
        }
        
        if(booking.orderIds.isEmpty()) {
            return true;
        }
        
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order == null || order.createByManager == null || (!order.createByManager.equals("PmsDailyOrderGeneration") && !order.createByManager.equals("SalesPoint"))) {
                return false;
            }
        }
        return true;
    }

    private void notifyAboutCreditedOrders(Order order, double total, PmsBooking booking) {
        List<String> emails = pmsManager.getConfigurationSecure().emailsToNotify.get("creditorder");
        String message = "Order " + order.incrementOrderId + " has been credited from external channel, total amount: " + total + "<br><br>";
        try {
            User user = userManager.getUserById(order.userId);
            message += "Name: " + user.fullName + ", " + user.emailAddress + ", " + "(" + user.prefix + ") " + user.cellPhone + "<br>";
            message += "Other orders connected to booking:<br>";
            for(String orderId : booking.orderIds) {
                Order otherOrder = orderManager.getOrderSecure(orderId);
                message += otherOrder.incrementOrderId + " (" + orderManager.getTotalAmount(otherOrder) + ")<br>";
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        if(emails != null) {
            for(String email : emails) {
                messageManager.sendMail(email, email, "Credited order", message, email, email);
            }
        }
    }

    private User getUserForBooking(PmsBooking booking) {
        User user = userManager.getUserById(booking.userId);
        if (user == null) {
            logPrint("User does not exists: " + booking.userId + " for booking : " + booking.id);
            Exception ex = new Exception();
            logPrintException(ex);
            messageManager.sendErrorNotification("User does not exists on booking, this has to be checked and fixed (userid: " + booking.userId + ").", ex);
            return null;
        }

        if(user.address == null) {
            user.address = new Address();
        }
        user.address.fullName = user.fullName;
        return user;
    }

    private String findPricePluginForBooking(PmsBooking booking) {
        try {
            PmsConfiguration config = pmsManager.getConfigurationSecure();
            if(booking.priceType.equals(PmsBooking.PriceType.daily)) {
                if(config.priceCalcPlugins.containsKey("dailypriceplugin")) {
                    return config.priceCalcPlugins.get("dailypriceplugin");
                }
            }
        }catch(Exception e) {
            logPrintException(e);
        }
        
        if(storeManager.isNewer(2018,2,1)) {
            return "pmsdailyordergeneration";
        }
        
        return "";

    }

    @Override
    public void saveStatisticsFilter(PmsOrderStatsFilter filter) {
        saveObject(filter);
        savedIncomeFilters.put(filter.id, filter);
    }

    @Override
    public List<PmsOrderStatsFilter> getAllStatisticsFilters() {
        return new ArrayList(savedIncomeFilters.values());
    }

    @Override
    public void deleteStatisticsFilter(String id) {
        PmsOrderStatsFilter filter = savedIncomeFilters.get(id);
        deleteObject(filter);
        savedIncomeFilters.remove(id);
    }

    private Double calculateLongTermDiscount(PmsBooking booking, Double price, PmsBookingRooms room) {
        if(booking.couponCode != null && !booking.couponCode.isEmpty()) {
            return price;
        }
        
        PmsPricing plan = pmsManager.getPriceObject(booking.pmsPricingCode);
        int percentages = 0;
        int daysUsed = 0;
        if(room != null) {
            for(Integer days : plan.getLongTermDeal().keySet()) {
                if(days <= room.getNumberOfNights() && daysUsed < days) {
                    percentages = plan.longTermDeal.get(days);
                }
            }
        }
        
        if(percentages > 0) {
            double factor = (100 - percentages)/(double)100;
            price *= factor;
        }
        
        return price;
    }

    private boolean isShortSpan(Date startDate, Date endDate) {
        if(startDate == null || endDate == null) {
            return false;
        }
        int totalDays = Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
        return totalDays < 15;
    }

    private boolean isNotRecurring(CartItem item) {
        return item.getProduct().isNotRecurring;
    }

    @Override
    public Order removeDuplicateOrderLines(Order order) {
        PmsOrderCleaner cleaner = new PmsOrderCleaner(order);
        return cleaner.cleanOrder();
    }
 
    public boolean isRoomPaidForWithBooking(String pmsRoomId, PmsBooking booking) {
        if(booking == null) {
            return false;
        }
        if(booking.payedFor) {
            return true;
        }
        
        boolean payedfor = true;
        boolean hasOrders = false;
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order == null) {
                continue;
            }
            if(order.isPrepaidByOTA()) {
                hasOrders = true;
                continue;
            }
            if(!hasRoomItems(pmsRoomId, order)) {
                continue;
            }
            hasOrders = true;
            if(order.status == Order.Status.PAYMENT_COMPLETED) {
                continue;
            }
            return false;
        } 
        
        if(!booking.isRecentlyCompleted() && booking.getRoom(pmsRoomId).totalCost == 0.0) {
            return true;
        }
       
        if(!hasOrders && (pmsManager.getConfigurationSecure().getMarkBookingsWithNoOrderAsUnpaid())) {
            payedfor = false;
        }
        
        
        
        return payedfor;
    }

    private void verifyOrderCreationIsCorrect(PmsBooking booking, Order order) {
        Double total = booking.getTotalPrice();
        
        Double totalOnOrders = 0.0;
        for(String orderId : booking.orderIds) {
            Order tmp = orderManager.getOrder(orderId);
            totalOnOrders += orderManager.getTotalAmount(tmp);
        }
        double diff = totalOnOrders - total;
        if(diff > 1 || diff < -1) {
            messageManager.sendErrorNotification("When creating an order, a diff where created that was not supposed to be ("+diff+"), this happened to order: " + order.incrementOrderId, null);
        }
        
        boolean warned = false;
        for(CartItem item : order.cart.getItems()) {
            double itemdiff = item.getDiffForFromMeta();
            if(itemdiff > 1.0 || itemdiff < -1.0) {
                if(!warned) {
                   String corrected = "no";
                   if(item.correctIncorrectCalculation()) {
                       corrected = "yes";
                   }
                   messageManager.sendErrorNotification("When creating an order, an item got incorrect metadata diff where created that was not supposed to be ("+itemdiff+"), this happened to order: " + order.incrementOrderId + "; corrected: " + corrected, null);
                   warned = true;
                }
            }
        }
        
    }

    private void adjustAmountOnOrder(Order order, Double totalAmount) {
        double now = orderManager.getTotalAmount(order);
        double diff = (totalAmount / now);

        for(CartItem item : order.cart.getItems()) {
            item.getProduct().price *= diff;
            if(item.itemsAdded != null) {
                for(PmsBookingAddonItem addonItem : item.itemsAdded) {
                    addonItem.price *= diff;
                }
            }
            if(item.priceMatrix != null) {
                for(String date : item.priceMatrix.keySet()) {
                    Double prices = item.priceMatrix.get(date);
                    prices *= diff;
                    item.priceMatrix.put(date, prices);
                }
            }
        }
    }

    private Double increasePriceOnCoverage(Double price, Date dateObject, PmsPricing prices) {
        Integer coverage = bookingEngine.getCoverageForDate(dateObject);
        
        Integer offset = 0;
        for(Integer cov : prices.coveragePrices.keySet()) {
            if(cov > offset && coverage > cov) {
                offset = cov;
            }
        }
        
        Double increase = 0.0;
        if(offset > 0) {
           increase = prices.coveragePrices.get(offset);
        }
        
        if(increase > 0) {
            if(prices.coverageType == 0) {
                price = price * (increase / 100);
            } else {
                price = price + increase;
            }
        }
        
        return price;
    }

    private void doubleCheckStats(PmsOrderStatistics stats, List<Order> orders) {
        for(Order order : orders) {
            Double totalEx = orderManager.getTotalAmount(order);
            Double totalInStats = stats.getTotalForOrder(order.id);
            Double diff = totalEx - totalInStats;
            if(diff < -1 || diff > 1) {
//                System.out.println("Order failed calculated: " + order.incrementOrderId + " - " + diff);
            }
        }
    }

    private List<Order> filterOrdersOnChannel(String channel, List<Order> orders) {
        List<PmsBooking> bookings = pmsManager.getAllBookings(null);

        List<Order> ordersToReturn = new ArrayList();
        
        HashMap<String, Order> ordersMap = new HashMap();
        for(Order ord : orders) {
            ordersMap.put(ord.id, ord);
        }
        List<String> added = new ArrayList();
        for(PmsBooking booking : bookings) {
            if(channel != null && channel.equals("web") && (booking.channel != null && !booking.channel.trim().isEmpty())) {
                for(String orderId : booking.orderIds) {
                    added.add(orderId);
                }
            } else if(booking.channel != null && booking.channel.equals(channel)) {
                for(String orderId : booking.orderIds) {
                    ordersToReturn.add(ordersMap.get(orderId));
                    added.add(orderId);
                }
            }
        }
        
        if(channel != null && channel.equals("web")) {
            for(Order ord : orders) {
                if(!added.contains(ord.id)) {
                    ordersToReturn.add(ord);
                }
            }
        }
        
        return ordersToReturn;
    }

    public Double getDerivedPrice(PmsBooking booking, String bookingItemType, Integer numberOfGuests, int adults, int children) {
        PmsPricing priceObject = pmsManager.getPriceObjectFromBooking(booking);
        double toAdd = 0.0;
        int childrenAdded = 0;
        if(priceObject.derivedPrices != null && priceObject.derivedPrices.containsKey(bookingItemType)) {
            HashMap<Integer, Double> derivedPriced = priceObject.derivedPrices.get(bookingItemType);
            HashMap<Integer, Double> derivedPricedChildren = null;
            if(priceObject.derivedPricesChildren != null) {
                derivedPricedChildren = priceObject.derivedPricesChildren.get(bookingItemType);
            }
            
            for(int i = 2;i <= numberOfGuests;i++) {
                Double toAddPrice = 0.0;
                if(derivedPriced != null && derivedPriced.containsKey(i)) {
                    toAddPrice = derivedPriced.get(i);
                }
                if(derivedPricedChildren != null && derivedPricedChildren.containsKey(i)) {
                    Double toAddPriceChildren = derivedPricedChildren.get(i);
                    if(toAddPriceChildren > 0 && toAddPrice > toAddPriceChildren && childrenAdded < children) {
                        toAddPrice = toAddPriceChildren;
                        childrenAdded++;
                    }
                }
                
                toAdd += toAddPrice;
            }
        }
        return toAdd;
    }

    private List<CartItem> createAddonsCartItemsToInvoiceForGetShop(List<PmsBookingAddonItem> items, PmsBookingRooms room) {
        List<CartItem> itemsToReturn = new ArrayList();
        
        for(PmsBookingAddonItem item : items) {
            CartItem itemToReturn = createCartItem(item.productId, null, room, item.date, item.date, item.price, item.count, null);
            if(itemToReturn != null) {
                
                if(item.getName() != null && !item.getName().isEmpty()) {
                    itemToReturn.getProduct().name = item.getName();
                }
                itemToReturn.startDate = null;
                itemToReturn.endDate = null;
                itemToReturn.getProduct().metaData = "";
                itemToReturn.getProduct().additionalMetaData = "";
                
                itemsToReturn.add(itemToReturn);
            }
        }

        return itemsToReturn;
    }

    @Override
    public void recalculateAllBookings(String password) {
        if(!password.equals("fdsafsadewuio8439ngdfs")) {
            return;
        }
        List<PmsBooking> bookings = pmsManager.getAllBookings(null);
        for(PmsBooking booking : bookings) {
            boolean recalc = false;
            for(String orderId : booking.orderIds) {
                Order order = orderManager.getOrder(orderId);
                if(order.closed) {
                    continue;
                }
                if(userManager.getUserById(booking.userId) == null) {
                    System.out.println("Cannot recalculate: " + booking.id);
                    continue;
                }
                order.cart.clear();
                orderManager.saveOrder(order);
                recalc = true;
            }
            if(recalc) {
                NewOrderFilter filter = new NewOrderFilter();
                filter.avoidOrderCreation = false;
                createOrder(booking.id, filter);
            }
        }
    }
    
    public List<String> getAllOrderIds(PmsBooking booking) {
        List<String> orderIds = booking.orderIds;
        orderIds.addAll(pmsManager.getExtraOrderIds(booking.id));
        return orderIds;
    }

    @Override
    public Double getTotalOnOrdersForRoom(String pmsRoomId, boolean inctaxes) {
        PmsBooking booking = pmsManager.getBookingFromRoomSecure(pmsRoomId);
        
        double total = 0;
        for(String orderId : getAllOrderIds(booking)) {
            Order order = orderManager.getOrderSecure(orderId);
            for(CartItem item : order.cart.getItems()) {
                String external = item.getProduct().externalReferenceId;
                if(external.equals(pmsRoomId)) {
                    if(inctaxes) {
                        total += item.getTotalAmount();
                    } else {
                        total += item.getTotalEx();
                    }
                }
            }
        }
        return total;
    }

    double getTotalUnpaidOnRoom(PmsBookingRooms room, PmsBooking booking, boolean avoidPaid) {
        double total = room.totalCost;
        for(String orderId : getAllOrderIds(booking)) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order != null) {
                if(order.status != Order.Status.PAYMENT_COMPLETED && avoidPaid) {
                    continue;
                }
                for(CartItem item : order.cart.getItemsUnfinalized()) {
                    if(item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId)) {
                        total -= item.getTotalAmount();
                    }
                }
            }
        }
        return total;
    }

    @Override
    public List<CartItem> getAllUnpaidItemsForRoom(String pmsRoomId) {
        NewOrderFilter filter = new NewOrderFilter();
        filter.pmsRoomId = pmsRoomId;
        filter.avoidOrderCreation = true;
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsRoomId);
        createOrder(booking.id, filter);
        
        List<CartItem> result = new ArrayList();
        result.addAll(cartManager.getCart().getItems());
        
        for(String orderId : getAllOrderIds(booking)) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order.status == Order.Status.PAYMENT_COMPLETED) {
                continue;
            }
            for(CartItem item : order.cart.getItems()) {
                if(item.getProduct().externalReferenceId.equals(pmsRoomId)) {
                    item.orderId = order.id;
                    result.add(item);
                }
            }
        }
        return result;
    }
        
    public Date getPaymentLinkSendingDate(String bookingId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        Calendar cal = Calendar.getInstance();
        cal.setTime(booking.getStartDate());
        cal.add(Calendar.DAY_OF_YEAR, pmsManager.getConfigurationSecure().numberOfDaysToSendPaymentLinkAheadOfStay * -1);
        if(cal.getTime().before(new Date())) {
            return new Date();
        } else {
            return cal.getTime();
        }
    }

    @Override
    public Double getTotalPaidOnRoomOrBooking(String pmsBookingRoomId) {
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsBookingRoomId);
        if(booking == null) {
            booking = pmsManager.getBooking(pmsBookingRoomId);
        }
        return 0.0;
    }

    @Override
    public PmsUserDiscount getUserDiscountByCouponCode(String couponCode) {
        if(couponCode == null || couponCode.trim().isEmpty()) {
            return null;
        }
        for(PmsUserDiscount disc : discounts.values()) {
            if(disc.attachedDiscountCode != null && disc.attachedDiscountCode.equals(couponCode)) {
                return disc;
            }
        }
        return null;
    }

    @Override
    public void saveAdvancePriceYield(PmsAdvancePriceYield yieldPlan) {
        saveObject(yieldPlan);
        advancePriceYields.put(yieldPlan.id, yieldPlan);
    }

    @Override
    public List<PmsAdvancePriceYield> getAllAdvancePriceYields() {
        return new ArrayList(advancePriceYields.values());
    }

    @Override
    public void deleteYieldPlan(String id) {
        PmsAdvancePriceYield plan = advancePriceYields.get(id);
        if(plan != null) {
            deleteObject(plan);
            advancePriceYields.remove(id);
        }
    }

    @Override
    public PmsAdvancePriceYield getAdvancePriceYieldPlan(String id) {
        return advancePriceYields.get(id);
    }

    private Double increasePriceByAdvanceCoverage(String typeId, Date start, Double price) {
        Integer coverage = null;
        
        if (useCacheCoverage()) {
            if(!savedCoverage.containsKey(start.getTime()/10000)) {
                coverage = bookingEngine.getCoverageForDate(start);
                savedCoverage.put(start.getTime()/10000, coverage);
            } else {
                coverage = savedCoverage.get(start.getTime()/10000);
            }
        } else {
            coverage = bookingEngine.getCoverageForDate(start);
        }
        
        AdvancePriceYieldCalculator calculator = new AdvancePriceYieldCalculator(advancePriceYields);
        return calculator.doCalculation(price, coverage,typeId, start);
    }

    public List<Order> getFilterOrders(PmsOrderStatsFilter filter) {
        List<Order> orders = new ArrayList();
        
        if(filter.includeVirtual) {
            pmsManager.createAllVirtualOrdersForPeriode(filter.start, filter.end);
            orders = orderManager.getAllOrderIncludedVirtual();
        } else {
            orders = orderManager.getAllOrders();
        }
        
        if(filter.channel != null && !filter.channel.trim().isEmpty()) {
            orders = filterOrdersOnChannel(filter.channel, orders);
        }
       
        List<Order> ordersToUse = new ArrayList();
        for(Order order : orders) {
            if(order == null || order.cart == null) {
                continue;
            }
            if(order.cart.getItems().isEmpty()) {
                continue;
            }
            if(order.testOrder) {
                continue;
            }
            if(filter.customers != null && !filter.customers.isEmpty()) {
                if(!filter.customers.contains(order.userId)) {
                    continue;
                }
            }
            
            if(filter.methods.isEmpty() || filter.includeVirtual) {
                ordersToUse.add(order);
            } else {
                for(PmsPaymentMethods pmethod : filter.methods) {
                    boolean avoid = false;
                    String filterMethod = pmethod.paymentMethod;
                    Integer filterStatus = pmethod.paymentStatus;
                    if(filterMethod != null && !filterMethod.isEmpty()) {
                        if(order.payment == null) {
                            avoid = true;
                        } else if(order.payment.paymentType != null) {
                            String method = filterMethod.replace("-", "_");
                            if(!order.payment.paymentType.contains(method)) {
                                avoid = true;
                            }
                        }
                    }

                    if(filterStatus != null) {
                        if(filterStatus == -10) {
                            if(!order.transferredToAccountingSystem) {
                                avoid = true;
                            }
                        }
                        if(filterStatus == -9) {
                            if(orderManager.getTotalAmount(order) > 0) {
                                avoid = true;
                            }
                        }

                        if(filterStatus > 0) {
                            if(order.status != filterStatus) {
                                avoid = true;
                            }
                        }
                    }
                    if(order.isVirtual && filter.includeVirtual || (order.isPrepaidByOTA() && filter.fromPmsModule)) {
                        avoid = false;
                    }

                    if(!avoid && !ordersToUse.contains(order)) {
                        ordersToUse.add(order);
                    }
                }
            }
        }
        return ordersToUse;
    }

    private boolean useCacheCoverage() {
        long millisecondSinceWubookStarted = Long.MAX_VALUE;
        long cacheValidFor60Seconds = 60000;
        
        if (cacheCoverage != null) {
            millisecondSinceWubookStarted = System.currentTimeMillis() - cacheCoverage.getTime();
        }
        
        if(millisecondSinceWubookStarted > cacheValidFor60Seconds) {
            savedCoverage.clear();
            return false;
        }
        
        return true;
    }

    public void startCacheCoverage() {
        cacheCoverage = new Date();
        savedCoverage.clear();
    }

    boolean hasUnchargedPrePaidOrders(PmsBookingRooms room, PmsBooking booking) {
        for(String orderId : getAllOrderIds(booking)) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order != null) {
                if(order.status == Order.Status.PAYMENT_COMPLETED) {
                    continue;
                }
                if(!order.isPrepaidByOTA()) {
                    continue;
                }
                
                if(order.getTotalAmount() == 0.0) {
                    continue;
                }
                
                for(CartItem item : order.cart.getItemsUnfinalized()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Double getTotalOrdersOnBooking(String bookingId) {
        PmsBooking booking = pmsManager.getBookingUnfinalized(bookingId);
        Double amount = 0.0;
        List<String> roomIds = booking.rooms.stream().map(e->e.pmsBookingRoomId).collect(Collectors.toList());
        
        for(String orderId : getAllOrderIds(booking)) {
            Order ord = orderManager.getOrderDirect(orderId);
            if(ord == null) { continue; }
            if(ord.isFromSamleFaktura()) {
                for(CartItem item : ord.getCartItems()) {
                    if(item.getProduct() != null && item.getProduct().externalReferenceId != null && roomIds.contains(item.getProduct().externalReferenceId)) {
                        amount += item.getTotalAmount();
                    }
                }
            } else {
                amount += orderManager.getTotalAmount(ord);
            }
        }
        return amount;
    }

    private void setDepartmentId(CartItem item, String roomId) {
        PmsBooking booking = pmsManager.getBookingFromRoom(roomId);
        
        if (booking != null) {
            PmsBookingRooms room = booking.getRoom(roomId);
            if (room != null) {
                BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                if (type != null) {
                    item.departmentId = type.departmentId;
                }
            }
        }
    }

    @Override
    public String getRedirectForBooking(String bookingId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        
        //Pay later button has been pressed.
        if(booking.payLater) { return "?page=payment_success"; }
        
        User usr = userManager.getUserById(booking.userId);
        
        //Booker is preferring this payment method.
        if(usr.preferredPaymentType != null && usr.preferredPaymentType.equals("70ace3f0-3981-11e3-aa6e-0800200c9a66")) {
            pmsManager.logEntry("Redirect to payment success due to preferred payment type invoice.", bookingId, null);
            return "/?page=payment_success";
        }
        
        //No orders has been created, needs to be a new version of the system where orders are not created.
        if(booking.orderIds.isEmpty()) {
            pmsManager.logEntry("Redirect to pr.php due to no orders.", bookingId, null);
            return "/pr.php?id=" + bookingId;
        }
        
        //Orders has been created, find an order and redirect to it.
        for(String orderId : booking.orderIds) {
            Order ord = orderManager.getOrderSecure(orderId);
            if(!ord.isFullyPaid() && !ord.isInvoice()) {
                pmsManager.logEntry("Redirect to payment since order is not paid, orderid:" + ord.incrementOrderId, bookingId, null);
                return "/?page=cart&payorder=" + ord.id;
            }
        }
        
        messageManager.sendErrorNotification("Failed to redirect booking : " + bookingId, null);
        return "";
    }

    class BookingOrderSummary {
        Integer count = 0;
        Double price = 0.0; 
        String productId = "";
    }

    @Override
    public void markOrderAsPaid(String bookingId, String orderId) {
        Order order = orderManager.getOrderSecure(orderId);
        order.status = Order.Status.PAYMENT_COMPLETED;
        order.payment.transactionLog.put(System.currentTimeMillis(), "Mark as paid for by pms");
        orderManager.saveOrder(order);
        pmsManager.logEntry("Order marked as paid for : " + order.incrementOrderId, bookingId, null);
        sendRecieptOnOrder(order, bookingId);
        pmsManager.processor();
    }

    public void autoSendInvoice(Order order, String bookingId) {
        if(!pmsManager.getConfigurationSecure().autoSendInvoice) {
            return;
        }
        if(order.sentToCustomer || order.sentToCustomer()) {
            return;
        }
        
        
        if(order.payment != null && order.payment.paymentType.toLowerCase().contains("invoice")) {
            if(!order.createdOnDay(new Date())) {
                order.closed = true;
                orderManager.saveOrder(order);
                sendRecieptOnOrder(order, bookingId);
            }
        }
    }
    
    @Override
    public List<Long> getOrdersForRoomToPay(String pmsRoomId) {
        PmsBooking booking = pmsManager.getBookingFromRoomSecure(pmsRoomId);
        if(booking == null) {
            return new ArrayList();
        }
        
        List<Long> res = new ArrayList();
        
        for(String orderId : getAllOrderIds(booking)) {
            Order order = orderManager.getOrderSecure(orderId);
            if(!hasRoomItems(pmsRoomId, order)) {
                continue;
            }

            if(order.status == Order.Status.PAYMENT_COMPLETED) {
                continue;
            }
            if(order.status == Order.Status.CANCELED) {
                continue;
            }
            res.add(order.incrementOrderId);
        }
        
        return res;
    }
    
    @Override
    public boolean isRoomPaidFor(String pmsRoomId) {
        PmsBooking booking = pmsManager.getBookingFromRoomSecure(pmsRoomId);
        return isRoomPaidForWithBooking(pmsRoomId, booking);
    }
    
    public boolean hasRoomItems(String pmsRoomId, Order order) {
        if(order == null || order.cart == null) {
            return false;
        }
        for(CartItem item : order.cart.getItems()) {
            if(item.getProduct().externalReferenceId == null) {
                continue;
            }
            String refId = item.getProduct().externalReferenceId;
            if(refId != null && refId.equals(pmsRoomId)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<CartItem> removeOrderLinesOnOrdersForBooking(String id, List<String> roomIds) {
        PmsBooking booking = pmsManager.getBooking(id);
        
        cartManager.clear();
        List<CartItem> allItemsToMove = new ArrayList();
        List<String> orderIdsToRemove = new ArrayList();
        for(String orderId : getAllOrderIds(booking)) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order.closed) {
                continue;
            }
            for(String roomId : roomIds) {
                List<CartItem> itemsToRemove = new ArrayList();
                for(CartItem item : order.cart.getItems()) {
                    String refId = item.getProduct().externalReferenceId;
                    if(refId != null && refId.equals(roomId)) {
                        itemsToRemove.add(item);
                    }
                }
                for(CartItem toRemove : itemsToRemove) {
                    order.cart.removeItem(toRemove.getCartItemId());
                }
                allItemsToMove.addAll(itemsToRemove);
            }
            orderManager.saveOrder(order);
            
            try {
                Double amount = orderManager.getTotalAmount(order);
                if(amount == 0.0) {
                    orderIdsToRemove.add(orderId);
                }
            }catch(Exception e) {

            }
        }
        
        if(!orderIdsToRemove.isEmpty()) {
            booking.orderIds.removeAll(orderIdsToRemove);
            pmsManager.saveBooking(booking);
        }
        
        
        
        return allItemsToMove;
    }

    public void sendRecieptOnOrder(Order order, String bookingId) {
        User user = userManager.getUserById(order.userId);
        String usersEmail = user.emailAddress;
        if(user.emailAddressToInvoice != null && !user.emailAddressToInvoice.isEmpty()) {
            usersEmail = user.emailAddressToInvoice;
        }

        if(order.recieptEmail != null && !order.recieptEmail.isEmpty()) {
            usersEmail = order.recieptEmail;
        }
        sendRecieptOrInvoice(order.id, usersEmail, bookingId);
        pmsManager.logEntry("Reciept / invoice sent to : " + usersEmail + " orderid: " + order.incrementOrderId, bookingId, null);
        
    }

    @Override
    public PmsOrderStatistics generateStatistics(PmsOrderStatsFilter filter) {
        if(filter == null) {
            return new PmsOrderStatistics(null, userManager.getAllUsersMap());
        }
        List<Order> ordersToUse = getFilterOrders(filter);
        double totalAmountEx = 0;
        for(Order ord : ordersToUse) {
            totalAmountEx += orderManager.getTotalAmountExTaxes(ord);
        }
        
        List<String> roomProducts = new ArrayList();
        for(BookingItemType type : bookingEngine.getBookingItemTypes()) {
            roomProducts.add(type.productId);
            roomProducts.addAll(type.historicalProductIds);
        }
        PmsOrderStatistics stats = new PmsOrderStatistics(roomProducts, userManager.getAllUsersMap());
        stats.createStatistics(ordersToUse, filter);
        doubleCheckStats(stats,ordersToUse);

        return stats;
    }

    @Override
    public List<String> validateAllInvoiceToDates() {
        List<PmsBooking> all = pmsManager.getAllBookings(null);
        List<String> result = new ArrayList();
        for(PmsBooking booking : all) {
            validateInvoiceToDateForBooking(booking, result);
        }
        return result;
    }

    public void validateInvoiceToDateForBooking(PmsBooking booking, List<String> result) throws ErrorException {
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.isEndedDaysAgo(60)) {
                continue;
            }
            Date invoicedTo = null;
            Long incordertouse = null;
            if(room.invoicedTo != null) {
                for(String orderId : booking.orderIds) {
                    if(!orderManager.orderExists(orderId)) {
                        continue;
                    }
                    Order order = orderManager.getOrderSecure(orderId);
                    if(order.isCreditNote) {
                        continue;
                    }
                    if(order.creditOrderId.size() > 0) {
                        continue;
                    }
                    if(order.cart == null) {
                        continue;
                    }
                    for(CartItem item : order.cart.getItems()) {
                        if(!item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId)) {
                            continue;
                        }
                        if(item.endDate == null) {
                            continue;
                        }
                        Date orderDateTo = item.endDate;
                        if(invoicedTo == null || invoicedTo.before(orderDateTo)) {
                            invoicedTo = orderDateTo;
                            incordertouse = order.incrementOrderId;
                        }
                    }
                }
                if(invoicedTo == null) {
                    room.invoicedTo = null;
                    continue;
                }
                
                if(pmsManager.getConfigurationSecure().substractOneDayOnOrder) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(invoicedTo);
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    invoicedTo = cal.getTime();
                }
                if(!room.isSameDay(room.invoicedTo, invoicedTo)) {
                    String item = "";
                    if(room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                        item = bookingEngine.getBookingItem(room.bookingItemId).bookingItemName;
                    }
                    String userName = userManager.getUserById(booking.userId).fullName;
                    
                    if(room.invoicedTo != null && !isSameDay(room.invoicedTo, invoicedTo)) {
                        String msg = item + " marked as invoiced to: " + new SimpleDateFormat("dd.MM.yyyy").format(room.invoicedTo) + ", but only invoiced to " + new SimpleDateFormat("dd.MM.yyyy").format(invoicedTo)  + " (" + incordertouse + ")" + ", user:" + userName;
                        result.add(msg);
                        room.invoicedTo = invoicedTo;
                        pmsManager.saveBooking(booking);
//                        messageManager.sendErrorNotification(msg, null);
                    }
                }
            }
        }
    }

    private Double addDerivedPrices(PmsBooking booking, PmsBookingRooms room, Double price) {
        if(room.bookingItemTypeId == null) {
            return price;
        }
        Double toAdd = getDerivedPrice(booking, room.bookingItemTypeId, room.numberOfGuests, room.getNumberOfAdults(), room.getNumberOfChildren());
        return price + toAdd;
    }

    public Double generatePriceFromPriceMatrix(HashMap<String, Double> priceMatrix, boolean avgPrice, PmsBooking booking, String typeId) {

        Double totalPrice = 0.0;
        int count = 0;
        for(Double tmpPrice : priceMatrix.values()) {
            totalPrice += tmpPrice;
            count++;
        }
        
        Double price = totalPrice;
        
//        price = calculateDiscounts(booking, price, typeId, count, null, null, null);
        
        
        if(avgPrice && count != 0) {
            price /= count;
        }
        
        if(price.isNaN() || price.isInfinite()) {
            logPrint("Nan price or infinite price... this is not good");
            price = 0.0;
        }
        return price;
    }

    public Double calculateDiscounts(PmsBooking booking, Double price, String bookingEngineTypeId, int count, PmsBookingRooms room, Date start, Date end) {
        if(room != null) { 
            price = addDerivedPrices(booking, room, price);
        }
        price = calculateLongTermDiscount(booking, price, room);
        double oldprice = price;
        price = calculateDiscountCouponPrice(booking, price, start, end, bookingEngineTypeId,room);
        if(oldprice == price) {
            price = getUserPrice(bookingEngineTypeId, price, count, booking);
        }

        return price;
    }

    public void clearOrdersOnBooking(PmsBooking booking) {
        List<String> nonRefundableRooms = new ArrayList();
        for(PmsBookingRooms r : booking.rooms) {
            if(r.nonrefundable) {
                nonRefundableRooms.add(r.pmsBookingRoomId);
                r.nonrefundable = false;
            }
        }
        List<Order> ordersToSave = new ArrayList();
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order.closed) {
                if(order.isPrepaidByOTA() && order.isRecent()) {
                    //This is specifically for wubook, should have been moved into wubookmanager.
                    order.closed = false;
                    order.status = Order.Status.WAITING_FOR_PAYMENT;
                    order.orderTransactions.clear();
                } else {
                    continue;
                }
            }
            order.cart.clear();
            ordersToSave.add(order);
        }
        for(Order save : ordersToSave) {
            orderManager.saveOrder(save);
        }
        for(String rid : nonRefundableRooms) {
            PmsBookingRooms tmpRoom = booking.getRoom(rid);
            tmpRoom.nonrefundable = true;
        }
        pmsManager.saveBooking(booking);
    }

    private Payment getPreferredPaymentTypeFromBooking(PmsBooking booking) {
        if(getSession() == null) {
            return null;
        }
        if(getSession().currentUser == null) {
            return null;
        }
        
        if(!getSession().currentUser.isAdministrator()) {
            return null;
        }
        if(booking.paymentType == null || booking.paymentType.isEmpty()) {
            return null;
        }
        
        Application paymentApplication = applicationPool.getApplication(booking.paymentType);
        if (paymentApplication != null) {
            Payment payment = new Payment();
            payment.paymentType = "ns_" + paymentApplication.id.replace("-", "_") + "\\" + paymentApplication.appName;
            payment.paymentId = paymentApplication.id;
            return payment;
        }
        
        return null;
    }

    public Payment getPreferredPaymentMethod(String bookingId, NewOrderFilter filter) {
        PmsBooking booking = pmsManager.getBooking(bookingId);
        Payment preferred = orderManager.getStorePreferredPayementMethod();
        Payment preferredChannel = null;
        if(filter != null && !filter.fromAdministrator) {
            preferredChannel = getChannelPreferredPaymentMethod(booking);
        }
        Payment preferredUser = orderManager.getUserPrefferedPaymentMethodOnly(booking.userId);
        
        Payment preferredBooking = getPreferredPaymentTypeFromBooking(booking);
        
        if(preferredChannel != null) {
            preferred = preferredChannel;
        }
        
        if(preferredUser != null) {
            preferred = preferredUser;
        }
        if(preferredBooking != null) {
            preferred = preferredBooking;
        }
        return preferred;
    }

    private List<Order> getOrdersFromRoom(String pmsRoomId) {
        List<Order> result = new ArrayList();
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsRoomId);
        for(String orderId : booking.orderIds) {
            result.add(orderManager.getOrderSecure(orderId));
        }
        return result;
    }

    private Double getTotalValue(Order latestOrder, String bookingItemId) {
        Double value = 0.0;
        for(CartItem item : latestOrder.cart.getItems()) {
            if(item.getProduct().externalReferenceId.equals(bookingItemId)) {
                value += item.getCount() * item.getProduct().price;
            }
        }
        return value;
    }
    
    private boolean avoidOrderCreation = false;
    private List<CartItem> itemsToReturn = new ArrayList();
    private HashMap<String, PmsUserDiscount> discounts = new HashMap();
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    CartManager cartManager;
    
    @Autowired
    MessageManager messageManager;
    
    @Autowired
    PmsDailyOrderGeneration pmsDailyOrderGeneration;
    
    private boolean runningDiffRoutine = false;
    

    @Override
    public String createRegisterCardOrder(String item) {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.startDate = new Date();
        filter.endDate = new Date();
        filter.filterType = "active";
        
        List<PmsBooking> bookings = pmsManager.getAllBookings(filter);
        List<PmsBooking> result = new ArrayList();
        for(PmsBooking booking : bookings) {
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                if(room.bookingItemId.equals(item)) {
                    result.add(booking);
                }
            }
        }
        
        if(result.size() != 1) {
            return "-1";
        }
        
        PmsBooking booking = result.get(0);
        
        User user = userManager.getUserById(booking.userId);
        user.preferredPaymentType = orderManager.getStorePreferredPayementMethod().paymentId;
        userManager.saveUserSecure(user);
        
        cartManager.clear();
        Order order = orderManager.createOrderForUser(booking.userId);
        return order.id;
    }
    
    
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof PmsUserDiscount) {
                PmsUserDiscount res = (PmsUserDiscount)dataCommon;
                discounts.put(res.userId, res);
            }
            if(dataCommon instanceof PmsAdvancePriceYield) {
                PmsAdvancePriceYield res = (PmsAdvancePriceYield)dataCommon;
                advancePriceYields.put(res.id, res);
            }
            if(dataCommon instanceof PmsOrderStatsFilter) {
                PmsOrderStatsFilter res = (PmsOrderStatsFilter)dataCommon;
                savedIncomeFilters.put(res.id, res);
            }
            if(dataCommon instanceof PmsPaymentLinksConfiguration) {
                PmsPaymentLinksConfiguration res = (PmsPaymentLinksConfiguration)dataCommon;
                paymentLinkConfig = res;
            }
        }
        createScheduler("checkinvoicedtodate", "1 04 * * *", DailyInvoiceChecker.class);
    }
    
    @Override
    public PmsUserDiscount getDiscountsForUser(String userId) {
        PmsUserDiscount discount = discounts.get(userId);
        if(discount == null) {
            discount = new PmsUserDiscount();
            discount.userId = userId;
            discounts.put(userId, discount);
            saveObject(discount);
        }
        
        return discount;
    }

    @Override
    public void saveDiscounts(PmsUserDiscount discount) {
        discounts.put(discount.userId, discount);
        saveObject(discount);
    }

    private List<BookingOrderSummary> summaries(List<CartItem> roomItems) {
        HashMap<String, BookingOrderSummary> res = new HashMap();
        for(CartItem item : roomItems) {
            BookingOrderSummary summary = null;
            if(item == null) {
                continue;
            }
            if(!res.containsKey(item.getProduct().id)) {
                summary = new BookingOrderSummary();
                res.put(item.getProduct().id, summary);
            } else {
                summary = res.get(item.getProduct().id);
            }
            summary.count += item.getCount();
            summary.price += (item.getProduct().price * item.getCount());
            summary.productId = item.getProduct().id;
        }
        
        return new ArrayList(res.values());
    }

    private List<BookingOrderSummary> diffItems(List<BookingOrderSummary> ordersummaries, List<BookingOrderSummary> roomSummaries) {
        
        HashMap<String, BookingOrderSummary> orderMap = createSummaryMap(ordersummaries);
        HashMap<String, BookingOrderSummary> roomMap = createSummaryMap(roomSummaries);
        
        for(String key : orderMap.keySet()) {
            if(!roomMap.containsKey(key)) {
                BookingOrderSummary summary = new BookingOrderSummary();
                summary.count = 0;
                summary.price = 0.0;
                summary.productId = key;
                roomMap.put(key, summary);
            }
        }
        
        List<BookingOrderSummary> list = new ArrayList();
        for(String productId : roomMap.keySet()) {
            BookingOrderSummary summaryResult = new BookingOrderSummary();
            summaryResult.count = roomMap.get(productId).count;
            summaryResult.price = roomMap.get(productId).price;
   
            if(orderMap.containsKey(productId)) {
                summaryResult.count -= orderMap.get(productId).count;
                summaryResult.price -= orderMap.get(productId).price;
            }
            
            summaryResult.productId = productId;
            list.add(summaryResult);
        }
        return list;
    }

    private HashMap<String, BookingOrderSummary> createSummaryMap(List<BookingOrderSummary> ordersummaries) {
        HashMap<String, BookingOrderSummary> res = new HashMap();
        for(BookingOrderSummary tmp : ordersummaries) {
            res.put(tmp.productId, tmp);
        }
        return res;
    }

    private List<CartItem> creditAllLinesOnBookingForRoom(String id, String pmsBookingRoomId) {
        List<CartItem> items = getAllOrderItemsForRoomOnBooking(pmsBookingRoomId, id);
        List<CartItem> credited = new ArrayList();
        for(CartItem item : items) {
            CartItem newItem = item.copy();
            newItem.setCount(newItem.getCount() * -1);
            credited.add(newItem);
        }
        return credited;
    }


    private LinkedHashMap<String, Double> getPriceMatrix(String typeId, Date start, Date end, Integer priceType, PmsBooking booking) {
        PmsPricing prices = pmsManager.getPriceObjectFromBooking(booking);
        LinkedHashMap<String, Double> price = new LinkedHashMap();
        if (prices.defaultPriceType == 1) {
            price = calculateDailyPricing(typeId, start, end, prices);
        }
        if (prices.defaultPriceType == 2) {
            price = calculateMonthlyPricing(typeId, start, end, prices);
        }
        if (prices.defaultPriceType == 7) {
            price = calculateProgressivePrice(typeId, start, end, 0, priceType, prices);
        }
        if (prices.defaultPriceType == 8) {
            price = calculateIntervalPrice(typeId, start, end, prices);
        }
        
        
        return price;
    }

    
    public void correctFaultyPriceMatrix(PmsBookingRooms room, PmsBooking booking) {
        logPrint("############ FAULTY PRICE MATRIX CREATED ######################");
        LinkedHashMap<String, Double> priceMatrix = getPriceMatrix(room.bookingItemTypeId, room.date.start, room.date.end, booking.priceType, booking);
        if(booking.priceType == PmsBooking.PriceType.daily) {
            for(String key : priceMatrix.keySet()) {
                room.priceMatrix.put(key, room.price);
            }
        }
    }
    
    @Override
    public HashMap<String, Double> calculatePriceMatrix(PmsBooking booking, PmsBookingRooms room) {
        updatePriceMatrix(booking, room, PmsBooking.PriceType.daily);
        return room.priceMatrix;
    } 
    
    public double updatePriceMatrix(PmsBooking booking, PmsBookingRooms room, Integer priceType) {
        pmsManager.checkAndReportPriceMatrix(booking, "when updating pricsudo n stablee matrix");
        LinkedHashMap<String, Double> priceMatrix = getPriceMatrix(room.bookingItemTypeId, room.date.start, room.date.end, priceType, booking);
        if(priceMatrix == null || priceMatrix.isEmpty()) {
            logPrint("Empty price matrix detected, we can't continue updating price matrix when the price matrix returned is empty, " + room.date.start + " - " + room.date.end + " pricetype: " + priceType + " bookingid: " + booking.id);
            return room.price;
        }
        double total = 0.0;
        int count = 0;
        if(priceType == PmsBooking.PriceType.daily) {
            for(String key : priceMatrix.keySet()) {
                if(!room.priceMatrix.containsKey(key) || !booking.isCompletedBooking()) {
                    Double price = priceMatrix.get(key);
                    Date day = PmsBookingRooms.convertOffsetToDate(key);
                    day = pmsManager.getConfigurationSecure().getDefaultStart(day);
                    price = calculateDiscounts(booking, price, room.bookingItemTypeId, 1, room, day, day);
                    room.priceMatrix.put(key, price);
                    priceMatrix.put(key, price);
                }
            }

            List<String> toRemoveExisting = new ArrayList();
            for(String existingKey : room.priceMatrix.keySet()) {
                if(!priceMatrix.containsKey(existingKey)) {
                    toRemoveExisting.add(existingKey);
                }
            }

            for(String key : toRemoveExisting) {
                room.priceMatrix.remove(key);
            }
        
            for(String key : priceMatrix.keySet()) {
                Double price = priceMatrix.get(key);
                total += price;
                count++;
            }
        }
        return total / count;
    }

    private Date adjustDateForCount(Date date, Integer priceType, boolean start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(priceType == PmsBooking.PriceType.daily || priceType == PmsBooking.PriceType.interval || priceType == PmsBooking.PriceType.progressive) {
            if(start) {
                cal.set(Calendar.HOUR_OF_DAY, 12);
            } else {
                cal.set(Calendar.HOUR_OF_DAY, 11);
            }
        }
        return cal.getTime();
        
    }

    private void checkIfNeedAdditionalStartInvoicing(PmsBookingRooms room) {
        PmsBooking booking = pmsManager.getBookingFromRoom(room.pmsBookingRoomId);
        if(room.invoicedFrom != null && 
                !room.isSameDay(room.invoicedFrom, room.date.start) && 
                room.date.start.before(room.invoicedFrom)) {
            avoidChangeInvoicedTo = true;
            Date start = room.date.start;
            Date end = room.invoicedFrom;
            if(end.after(room.date.end)) {
                end = room.date.end;
            }
            
            createCartItemsForRoom(start, end, booking, room, null);
            avoidChangeInvoicedTo = false;
        }
    }

    private void checkIfNeedAdditionalEndInvoicing(PmsBookingRooms room, NewOrderFilter filter) {
         avoidChangingInvoicedFrom = true;
        PmsBooking booking = pmsManager.getBookingFromRoom(room.pmsBookingRoomId);

        Date startDate = room.getInvoiceStartDate();
        Date endDate = room.getInvoiceEndDate(filter, booking);
        
        if(room.invoicedTo != null && (room.isSameDay(room.invoicedTo, endDate) || (room.invoicedTo.after(endDate)) || room.isSameDay(room.invoicedTo, endDate))) {
            return;
        }
        if(endDate != null && filter.endInvoiceAt != null && endDate.after(filter.endInvoiceAt) && filter.increaseUnits > 0 && !pmsManager.getConfigurationSecure().splitOrderIntoMonths) {
            return;
        }
        if(room.invoicedTo == null && startDate.after(endDate)) {
            //Never invoiced, and a credit note is needed?
            return;
        }
        
        if(pmsManager.getConfigurationSecure().ignoreRoomToEndDate && endDate.before(filter.endInvoiceAt)) {
            endDate = filter.endInvoiceAt;
        }
        if(filter.endInvoiceAt != null && startDate.after(filter.endInvoiceAt)) {
            //Why should it be possible to invoice a stay with an end date that is before the start date of the stay?
            return;
        }
        
        List<CartItem> items = createCartItemsForRoom(startDate,endDate, booking, room, filter);

        if (pmsManager.getConfigurationSecure().substractOneDayOnOrder && !filter.onlyEnded) {
            for(CartItem item : items) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(item.endDate);
                cal.add(Calendar.DAY_OF_YEAR, -1);
                item.endDate = cal.getTime();
            }
        }
        avoidChangingInvoicedFrom = false;
    }

    private void autoGenerateOrders(PmsBookingRooms room, NewOrderFilter filter) {
        if(room.needInvoicing(filter)) {
            BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
            if(item != null) {
                logPrint("Item: " + item.bookingItemName);
            }
            checkIfNeedAdditionalEndInvoicing(room, filter);
        }
    }

    private Payment getChannelPreferredPaymentMethod(PmsBooking booking) {
        String channelPaymentId = pmsManager.getConfigurationSecure().getChannelConfiguration(booking.channel).preferredPaymentType;
        if(channelPaymentId != null) {
            Application paymentApplication = applicationPool.getApplication(channelPaymentId);
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
    public void creditOrder(String bookingId, String orderId) {
        Order currentOrder = orderManager.getOrderSecure(orderId);
        Order creditedOrder = orderManager.creditOrder(orderId);
        PmsBooking booking = pmsManager.getBooking(bookingId);
        
        
        if(!booking.ignoreCheckChangesInBooking) {
            for(CartItem item : creditedOrder.cart.getItems()) {
                String roomId = item.getProduct().externalReferenceId;
                PmsBookingRooms room = booking.getRoom(roomId);
                if(room == null) {
                    continue;
                }
                if(room.isSameDay(item.getStartingDate(), room.date.start)) {
                    room.invoicedTo = null;
                    room.invoicedFrom = null;
                } else {
                    room.invoicedTo = item.getStartingDate();
                }
            }
        } else {
            for(PmsBookingRooms room : booking.rooms) {
                room.invoicedTo = room.date.start;
            }
        }
        
        pmsManager.addOrderToBooking(booking, creditedOrder.id);
        pmsManager.saveBooking(booking);
    }
    
    @Override
    public void clearOrder(String bookingId, String orderId) {
        if(orderId.equals("createafterstay")) {
            return;
        }
        Order currentOrder = orderManager.getOrderSecure(orderId);
        if(currentOrder.closed) {
            return;
        }
        PmsBooking booking = pmsManager.getBooking(bookingId);
        
        for(CartItem item : currentOrder.cart.getItems()) {
            String roomId = item.getProduct().externalReferenceId;
            PmsBookingRooms room = booking.getRoom(roomId);
            if(room == null) {
                continue;
            }
            if(room.isSameDay(item.getStartingDate(), room.date.start)) {
                room.invoicedTo = null;
                room.invoicedFrom = null;
            } else {
                room.invoicedTo = item.getStartingDate();
            }
        }
        
        currentOrder.cart.clear();
        orderManager.saveOrder(currentOrder);
        pmsManager.saveBooking(booking);
    }

    private void addItemToItemsToReturn(CartItem item) {
        itemsToReturn.add(item);
    }

    private void addItemsToItemToReturn(List<CartItem> returnresult) {
        itemsToReturn.addAll(returnresult);
    }

    public Date normalizeDate(Date date, boolean startdate) {
        if(!pmsManager.getConfigurationSecure().bookingTimeInterval.equals(PmsConfiguration.PmsBookingTimeInterval.DAILY)) {
            return date;
        }
        
        Calendar cal = Calendar.getInstance();
        
        cal.setTime(date);
        if(startdate) {
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.HOUR_OF_DAY, 15);
        } else {
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.HOUR_OF_DAY, 12);
        }
        
        return cal.getTime();
    }

    @Override
    public String sendRecieptOrInvoice(String orderId, String email, String bookingId) {
        return sendRecieptOrInvoiceWithMessage(orderId, email, bookingId, null, null);
    }
    
    @Override
    public String sendRecieptOrInvoiceWithMessage(String orderId, String email, String bookingId, String message, String subject) {
        Order order = orderManager.getOrderSecure(orderId);
        orderManager.saveObject(order);
        String res = "";
        if(order.payment != null && order.payment.paymentType != null && order.payment.paymentType.toLowerCase().contains("sendregning")) {
            res = sendRegningManager.sendOrder(orderId, email);
        } else {
            pmsManager.setOrderIdToSend(orderId);
            pmsManager.setEmailToSendTo(email);
            if(message != null) {
                pmsNotificationManager.setEmailMessageToSend(message);
                pmsNotificationManager.setEmailSubject(subject);
            }
            if(order.status == Order.Status.PAYMENT_COMPLETED) {
                pmsManager.doNotification("sendreciept", bookingId);
            } else {
                pmsManager.doNotification("sendinvoice", bookingId);
            }
        }
        
        if(res.isEmpty()) {
            order.closed = true;
            order.markAsSent(OrderShipmentLogEntry.Type.email, email);
            orderManager.saveOrderInternal(order);
        }
        return res;
    }

    public String createOrder(String bookingId, NewOrderFilter filter) {
        
        if (storeManager.supportsCreateOrderOnDemand()) {
            // Start using PmsInvoiceManagerNew. Also try to make orders on demands instead of
            // when bookings etc are created.
            return "";
        }
        
        PmsBooking booking = pmsManager.getBooking(bookingId);
        if(filter.addToOrderId != null && !filter.addToOrderId.isEmpty()) {
            if(filter.addToOrderId.equals("createafterstay")) {
                booking.createOrderAfterStay = true;
                booking.paymentType = filter.paymentType;
                return "";
            }
        }
        
        if(booking != null && supportsDailyPmsInvoiceing(booking.id)) {
            if(filter.addToOrderId != null && !filter.addToOrderId.isEmpty()) {
                Order order = orderManager.getOrderSecure(filter.addToOrderId);
                if(order.attachedToRoom != null && !order.attachedToRoom.isEmpty()) {
                    filter.pmsRoomId = order.attachedToRoom;
                }
            }
            pmsDailyOrderGeneration.createCart(bookingId, filter);
        } else {
            return createOrderOld(bookingId, filter);
        }
        
        if(!filter.avoidOrderCreation) {
            Order order = createOrderFromCartNew(booking, filter, false);
            if(order == null) {
                return "";
            }
            order.wubookid = booking.latestwubookreservationid;
            order.createByManager = "PmsDailyOrderGeneration";
            try {
                order.shippingDate = getPaymentLinkSendingDate(booking.id);
            }catch(Exception e) {
                logPrintException(e);
            }
            if(filter.totalAmount != null && filter.totalAmount > 0) {
                adjustAmountOnOrder(order, filter.totalAmount);
            }
            orderManager.saveOrder(order);
            pmsManager.addOrderToBooking(booking, order.id);
            List<String> uniqueList = new ArrayList<String>(new HashSet<String>( booking.orderIds ));
            booking.orderIds = uniqueList;
            pmsManager.saveBooking(booking);
            
            try {
                if((filter.itemsToCreate == null || filter.itemsToCreate.isEmpty()) && 
                    (filter.pmsRoomIds == null || filter.pmsRoomIds.isEmpty()) &&
                        (filter.pmsRoomId != null && filter.pmsRoomId.isEmpty()) && 
                        filter.totalAmount == null) {
                    boolean warn = true;
                    if(filter.startInvoiceAt != null && !PmsBookingRooms.isSameDayStatic(filter.startInvoiceAt, booking.getStartDate())) {
                        warn = false;
                    }
                    if(filter.endInvoiceAt != null && !PmsBookingRooms.isSameDayStatic(filter.endInvoiceAt, booking.getEndDate())) {
                        warn = false;
                    }
                    if(warn) {
                      verifyOrderCreationIsCorrect(booking, order);
                    }
                }
            }catch(Exception e) {
                logPrintException(e);
            }
            
//            messageManager.sendErrorNotification("New order created: " + order.incrementOrderId, null);
            
            return order.id;
        }
        return "";
    }
    
    public void updateAddonsByDates(PmsBookingRooms room) {
        if(!pmsManager.getConfigurationSecure().hasAddons()) {
            return;
        }
        List<PmsBookingAddonItem> toRemove = new ArrayList();
        HashMap<Integer, Integer> addTypes = new HashMap();
        for(PmsBookingAddonItem addon : room.addons) {
            if(addon.addonType != null && pmsManager.getConfigurationSecure().addonConfiguration.get(addon.addonType).isSingle) {
                if(addon.addonType == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
                    addon.date = room.date.end;
                }
                if(addon.addonType == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) {
                    addon.date = room.date.start;
                }
                continue;
            }
            if(addon.date == null) { logPrint("Null date on addon, should not happen"); continue; }
            if(room.date == null) { logPrint("room null, should not happen"); continue; }
            if(room.date.start == null) { logPrint("room start date null, should not happen"); continue; }
            if(room.date.end == null) { logPrint("room start date null, should not happen"); continue; }
            
            if(addon.addonType == PmsBookingAddonItem.AddonTypes.BREAKFAST) {
                if(room.isSameDay(addon.date, room.date.end)) {
                    toRemove.add(addon);
                } else {
                    if(addon.count > 0 && room.guests.size() > 0) {
                        addon.count = room.numberOfGuests;
                    }
                }
            }
            
            if(addon.date.before(room.date.start) || addon.date.after(room.date.end) &&
                    (!room.isSameDay(addon.date, room.date.start) && !room.isSameDay(addon.date, room.date.end))) {
                toRemove.add(addon);
            }
            addTypes.put(addon.addonType, 1);
        }
        room.addons.removeAll(toRemove);
        
        java.util.Calendar startCal = java.util.Calendar.getInstance();
        startCal.setTime(room.date.start);
        startCal.set(Calendar.HOUR_OF_DAY, 23);
        startCal.set(Calendar.MINUTE, 59);
        while(true) {
            Date time = startCal.getTime();
            for(Integer key : addTypes.keySet()) {
                PmsBookingAddonItem config = pmsManager.getConfigurationSecure().addonConfiguration.get(key);
                if(room.hasAddon(key, startCal.getTime()) == null) {
                    PmsBookingAddonItem addon = pmsManager.createAddonToAdd(config, time, room);
                    room.addons.add(addon);
                }
            }
            
            startCal.add(java.util.Calendar.DAY_OF_YEAR, 1);
            if(startCal.getTime().after(room.date.end)) {
                break;
            }
        }
        
        room.sortAddonList();
    }

    
    private void addBookingToCart(PmsBooking booking, NewOrderFilter filter) {
        List<CartItem> items = new ArrayList();
        boolean generateChanges = pmsManager.getConfigurationSecure().autoGenerateChangeOrders;
        if(generateChanges) {
            if(!booking.ignoreCheckChangesInBooking && !filter.ignoreCheckChangesInBooking) {
                List<CartItem> changes = getChangesForBooking(booking.id, filter);
                items.addAll(changes);
                
                //@TODO Make it global for all, not only for kongsvingerbudghotell
                if(storeId.equals("9dda21a8-0a72-4a8c-b827-6ba0f2e6abc0")) {
                    List<CartItem> getLostItems = getLostItems(booking);
                    items.addAll(getLostItems);
                }
            }
        }
        
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if(filter.pmsRoomId != null && !filter.pmsRoomId.isEmpty()) {
                if(!filter.pmsRoomId.equals(room.pmsBookingRoomId)) {
                    continue;
                }
            }
            if(filter.autoGeneration) {
                autoGenerateOrders(room, filter);
            } else {
                if(filter.onlyEnded && !room.isEnded(filter.endInvoiceAt)) {
                    continue;
                }
                if(generateChanges) {
                    checkIfNeedCrediting(room);
                    checkIfNeedAdditionalStartInvoicing(room);
                }
                checkIfNeedAdditionalEndInvoicing(room, filter);
                if(itemsToReturn.isEmpty()) {
                    continue;
                }
            }
        }
    }
    
    public double calculateTaxes(String bookingItemTypeId) {
        BookingItemType type = bookingEngine.getBookingItemType(bookingItemTypeId);
        if (type.productId != null && !type.productId.isEmpty()) {
            Product product = productManager.getProduct(type.productId);
            if (product != null && product.taxGroupObject != null) {
                return product.taxGroupObject.taxRate;
            }
            return -1.0;
        }
        return -2.0;
    }

    private CartItem createCartItem(String productId, String name, PmsBookingRooms room, Date startDate, Date endDate, Double price, int count, String groupId) {
        if(price.isNaN() || price.isInfinite()) {
            logPrint("Trying to create cart item with infinite or NaN price");
            price = 0.0;
        }
        
        Double total = price * count;
        
        if(total.intValue() == 0) {
            return null;
        }
        BookingItem bookingitem = null;
        if (room.bookingItemId != null) {
            bookingitem = bookingEngine.getBookingItem(room.bookingItemId);
        }

        if (productId == null) {
            logPrint("Product not set for this booking item type");
            return null;
        }
        int numberOfDays = getNumberOfDays(startDate, endDate);
        if (numberOfDays == 0) {
            return null;
        }
    
        CartItem item = createCartItemForCart(productId, count, room.pmsBookingRoomId);
        if(item == null) {
            return null;
        }
        item.startDate = startDate;
        item.endDate = endDate;
        item.periodeStart = room.date.start;
        item.groupedById = groupId;
        
        if(item.startDate.after(item.endDate)) {
            Date tmpDate = item.startDate;
            item.startDate = endDate;
            item.endDate = tmpDate;
        }
        
        
        item.getProduct().price = price;
        
        if(name != null && !name.equals("null")) {
            item.getProduct().name = name;
        }
        if (bookingitem != null) {
            item.getProduct().additionalMetaData = bookingitem.bookingItemName;
        } else {
            item.getProduct().additionalMetaData = "";
        }
        
        String guestName = "";
        if (room.guests.size() > 0) {
            guestName = room.guests.get(0).name;
        }
        
        item.getProduct().discountedPrice = price;
        item.getProduct().price = price;
        item.getProduct().metaData = guestName;
        return item;
    }

    
    public int getNumberOfDays(Date startDate, Date endDate) {
        if(startDate.after(endDate)) {
            Date tmpStart = startDate;
            startDate = endDate;
            endDate = tmpStart;
        }
        
        startDate = adjustDateForCount(startDate, PmsBooking.PriceType.daily, true);
        endDate = adjustDateForCount(endDate, PmsBooking.PriceType.daily, true);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        
        int days = 0;
        while (true) {
            days++;

            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (cal.getTime().after(endDate) || cal.getTime().equals(endDate)) {
                break;
            }
        }
        return days;
    }

    private boolean shouldBeProcessed(PmsBooking booking) {
        if (booking.getActiveRooms() == null) {
            return false;
        }
        if (booking.isDeleted) {
            return false;
        }
        if (booking.sessionId != null && !booking.sessionId.isEmpty()) {
            return false;
        }
        if (!booking.confirmed) {
            return false;
        }
        if (!booking.payedFor && !pmsManager.getConfigurationSecure().ignoreRoomToEndDate) {
            return false;
        }
        return true;
    }

    private Order createOrderFromCart(PmsBooking booking, NewOrderFilter filter, boolean virtual) {
       
        User user = userManager.getUserById(booking.userId);
        if (user == null) {
            logPrint("User does not exists: " + booking.userId + " for booking : " + booking.id);
            Exception ex = new Exception();
            logPrintException(ex);
            messageManager.sendErrorNotification("User does not exists on booking, this has to be checked and fixed (userid: " + booking.userId + ").", ex);
            return null;
        }

        if(user.address == null) {
            user.address = new Address();
        }
        user.address.fullName = user.fullName;

        VirtualOrder virtualOrder = null;
        Order order = null;
        
        orderManager.deleteVirtualOrders(booking.id);
        
        if (virtual) {
            virtualOrder = orderManager.createVirtualOrder(user.address, booking.id);
            order = virtualOrder.order;
        } else {
            order = orderManager.createOrder(user.address);
        }
        
        order.userId = booking.userId;

        Payment preferred = getPreferredPaymentMethod(booking.id, filter);
        
        if(filter != null && filter.paymentType != null && !filter.paymentType.isEmpty() && !filter.paymentType.startsWith("savedcard_")) {
            preferred = getOverriddenPaymentType(filter);
        }
        
        order.payment = preferred;
        order.invoiceNote = booking.invoiceNote;
        
        if (pmsManager.getConfigurationSecure().substractOneDayOnOrder) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.rowCreatedDate);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            order.rowCreatedDate = cal.getTime();
        }
        
        order.dueDays = booking.dueDays;
        
        orderManager.setCompanyAsCartIfUserAddressIsNullAndUserConnectedToACompany(order, user.id);

        if (virtual) {
            orderManager.saveVirtalOrder(virtualOrder);
        } else {
            orderManager.saveOrder(order);
        }
        return order;
    }

    private Order createOrderFromCartNew(PmsBooking booking, NewOrderFilter filter, boolean virtual) {
       
        User user = getUserForBooking(booking);

        VirtualOrder virtualOrder = null;
        Order order = null;
        
        orderManager.deleteVirtualOrders(booking.id);
        
        if (virtual) {
            virtualOrder = orderManager.createVirtualOrder(user.address, booking.id);
            order = virtualOrder.order;
        } else {
            if(!filter.createNewOrder) {
                for(String tmpOrderId : booking.orderIds) {
                    Order tmpOrder = orderManager.getOrderSecure(tmpOrderId);
                    if(!tmpOrder.closed) {
                        order = tmpOrder;
                        break;
                    }
                }
            }
            
            if(filter.addToOrderId != null && !filter.addToOrderId.isEmpty()) {
                order = orderManager.getOrderSecure(filter.addToOrderId);
                if(order.closed) {
                    order = null;
                }
            }
            
            if(order != null && !order.closed) {
                order.cart.addCartItems(cartManager.getCart().getItems());
            } else {
                if(order == null) {
                    if (!cartManager.getCart().getItems().isEmpty()) {
                        order = orderManager.createOrder(user.address);
                    }
                    
                    
                    Double newAmount = orderManager.getTotalAmount(order);
                    
                    if(filter.addToOrderId != null && !filter.addToOrderId.isEmpty() && newAmount < 0.0) {
                        order.parentOrder = filter.addToOrderId;
                        orderManager.saveOrder(order);
                    }
                }
            }
        }
        
        if(order == null) {
            return null;
        }
        
        order.userId = booking.userId;
        
        if(filter.userId != null && !filter.userId.isEmpty()) { 
           order.userId = filter.userId;
        }
        
        autoSendInvoice(order, booking.id);

        Payment preferred = getPreferredPaymentMethod(booking.id, filter);
        
        if(filter.paymentType != null && !filter.paymentType.isEmpty() && !filter.paymentType.startsWith("savedcard_")) {
            preferred = getOverriddenPaymentType(filter);
        }
        
        order.payment = preferred;
        order.invoiceNote = booking.invoiceNote;
        
        Double total = orderManager.getTotalAmount(order);
        if(total < 0) {
            notifyAboutCreditedOrders(order, total, booking);
            
            if(filter.addToOrderId != null && !filter.addToOrderId.isEmpty()) {
                Order baseOrder = orderManager.getOrderSecure(filter.addToOrderId);
                baseOrder.creditOrderId.add(order.id);
                order.payment = baseOrder.payment;
            }
        }
        
        if(filter.pmsRoomId != null && !filter.pmsRoomId.isEmpty()) {
            order.attachedToRoom = filter.pmsRoomId;
        }

        if (pmsManager.getConfigurationSecure().substractOneDayOnOrder) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.rowCreatedDate);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            order.rowCreatedDate = cal.getTime();
        }
        
        orderManager.setCompanyAsCartIfUserAddressIsNullAndUserConnectedToACompany(order, user.id);

        if (virtual) {
            orderManager.saveVirtalOrder(virtualOrder);
        } else {
            orderManager.saveOrder(order);
        }
         
        return order;
    }

    private LinkedHashMap<String, Double> calculateProgressivePrice(String typeId, Date start, Date end, int offset, Integer priceType, PmsPricing prices) {
        ArrayList<ProgressivePriceAttribute> priceRange = prices.progressivePrices.get(typeId);
        LinkedHashMap<String, Double> result = new LinkedHashMap();
        if (priceRange == null) {
            logPrint("No progressive price found for type");
            return result;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int days = offset;
        while (true) {
            String dateToUse = PmsBookingRooms.getOffsetKey(cal, PmsBooking.PriceType.interval);
            
            int daysoffset = 0;
            for (ProgressivePriceAttribute attr : priceRange) {
                daysoffset += attr.numberOfTimeSlots;
                if (daysoffset > days) {
                    result.put(dateToUse, attr.price);
                    daysoffset = 0;
                    break;
                }
            }
            days++;
            if(priceType.equals(PmsBooking.PriceType.daily)) { cal.add(Calendar.DAY_OF_YEAR, 1); }
            else if(priceType.equals(PmsBooking.PriceType.weekly)) { cal.add(Calendar.DAY_OF_YEAR, 7); }
            else if(priceType.equals(PmsBooking.PriceType.monthly)) { cal.add(Calendar.MONTH, 1); }
            else if(priceType.equals(PmsBooking.PriceType.hourly)) { cal.add(Calendar.HOUR, 1); }
            else { cal.add(Calendar.DAY_OF_YEAR, 1); }
            if (end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
            if(priceType.equals(PmsBooking.PriceType.daily) || priceType.equals(PmsBooking.PriceType.progressive)) { 
                if(isSameDay(cal.getTime(), end)) {
                    break;
                }
            }
        }

        return result;
    }

    public boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private int getNumberOfMonthsBetweenDates(Date startDate, Date endDate) {
        int months = 1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        while(true) {
            cal.add(Calendar.MONTH, 1);
            if(cal.getTime().after(endDate)) {
                break;
            }
            if(cal.getTime().equals(endDate)) {
                break;
            }
            months++;
        }
        return months;
    }
    
    public Double calculatePrice(String typeId, Date start, Date end, boolean avgPrice, PmsBooking booking) {
        HashMap<String, Double> priceMatrix = getPriceMatrix(typeId, start, end, booking.priceType, booking);
        return generatePriceFromPriceMatrix(priceMatrix, avgPrice, booking, typeId);
    }

    private Double calculateDiscountCouponPrice(PmsBooking booking, Double price, Date start, Date end, String typeId, PmsBookingRooms room) {
        if(booking.couponCode != null && !booking.couponCode.isEmpty()) {
            String couponCode = booking.couponCode;
            if(booking.discountType.equals("partnership")) {
                if(couponCode.indexOf(":") >= 0) {
                    couponCode = "partnership:" + couponCode.substring(0, couponCode.indexOf(":"));
                }
            }
            Coupon coupon = cartManager.getCoupon(couponCode);
            if(coupon != null) {
                String productId = null;
                if(typeId != null) {
                    BookingItemType type = bookingEngine.getBookingItemType(typeId);
                    if(type != null) {
                        productId = type.productId;
                    }
                }
                int days = 1;
                Integer guestCount = 0;
                if(room != null) {
                    days = getNumberOfDays(room.date.start, room.date.end);
                    guestCount = room.numberOfGuests;
                }
                if(cartManager.couponIsValid(booking.rowCreatedDate, couponCode, start,end, productId, days)) {
                    price = cartManager.calculatePriceForCouponWithoutSubstract(couponCode, price, days, guestCount, typeId);
                }
            }
        }
            
        return price;
    }

    
    private Double getUserPrice(String typeId, Double price, int count, PmsBooking booking) {
        User user = null;
        if(getSession() != null && getSession().currentUser != null) {
            user = getSession().currentUser;
        }
        
        if(booking.userId != null && !booking.userId.isEmpty()) {
            user = userManager.getUserById(booking.userId);
        }
        
        if(user != null) {
            PmsUserDiscount discountForUser = getDiscountsForUser(user.id);
            Double discount = discountForUser.discounts.get(typeId);
            if(discount != null) {
                if(discountForUser.discountType.equals(PmsUserDiscount.PmsUserDiscountType.percentage)) {
                    price = price - (price * ((double)discount / 100));
                } else {
                    price = discount * count;
                }
            }
            if(user.showExTaxes) {
                Product product = productManager.getProduct(bookingEngine.getBookingItemType(typeId).productId);
                price = price * ((100+product.taxGroupObject.taxRate) / 100);
            }
       }
       return price;
    }

    
    private LinkedHashMap<String, Double> calculateDailyPricing(String typeId, Date start, Date end, PmsPricing prices) {
        HashMap<String, Double> priceRange = prices.dailyPrices.get(typeId);

        
        LinkedHashMap<String, Double> result = new LinkedHashMap();
        if(typeId.equals("gsconference")) {
            priceRange = new HashMap();
        }
        if (priceRange == null) {
            return result;
        }

        Double defaultPrice = priceRange.get("default");
        
        if (defaultPrice == null) {
            defaultPrice = 0.0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        Double price = 0.0;
        while (true) {
            String dateToUse = PmsBookingRooms.getOffsetKey(cal, PmsBooking.PriceType.daily);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            if (priceRange.get(dateToUse) != null) {
                price = priceRange.get(dateToUse);
            } else {
                price = defaultPrice;
            }
            
            if(pmsManager.getConfigurationSecure().enableCoveragePrices) {
                price = increasePriceOnCoverage(price, cal.getTime(), prices);
            }
            price = increasePriceByAdvanceCoverage(typeId, start, price);
            cal.add(Calendar.DAY_OF_YEAR,1);
            
            result.put(dateToUse, price);
            
            if (end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
        }
        return result;
    }

    
    private LinkedHashMap<String, Double> calculateMonthlyPricing(String typeId, Date start, Date end, PmsPricing prices) {
        HashMap<String, Double> priceRange = prices.dailyPrices.get(typeId);
        LinkedHashMap<String, Double> result = new LinkedHashMap();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        while(true) {
            Double price = 0.0;
            String toUse = PmsBookingRooms.getOffsetKey(cal, PmsBooking.PriceType.monthly);
            cal.add(Calendar.MONTH, 1);
            if(priceRange == null) {
                price = 0.0;
            } else if(!priceRange.containsKey(toUse)) {
                price = priceRange.get("default");
            } else {
                price = priceRange.get(toUse);
            }
            result.put(toUse, price);
            if(end ==  null || cal.getTime().after(end)) {
                break;
            }
        }
       
        
        return result;
    }

    private LinkedHashMap<String, Double> calculateIntervalPrice(String typeId, Date start, Date end, PmsPricing prices) {
        int totalDays = Days.daysBetween(new LocalDate(start), new LocalDate(end)).getDays();
        LinkedHashMap<String, Double> res = new LinkedHashMap();
        ArrayList<ProgressivePriceAttribute> priceRange = prices.progressivePrices.get(typeId);
        if (priceRange == null) {
            logPrint("No progressive price found for type");
            return res;
        }
        int daysoffset = 0;
        Double price = 0.0;
        for (ProgressivePriceAttribute attr : priceRange) {
            daysoffset += attr.numberOfTimeSlots;
            if (daysoffset >= totalDays) {
                price = attr.price;
                break;
            }
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        while (true) {
            String dateToUse = PmsBookingRooms.getOffsetKey(cal, PmsBooking.PriceType.interval);
            res.put(dateToUse, price);
            
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
        }
        //Could not find price to use.
        return res;
    }

    String createPrePaymentOrder(PmsBooking booking) {
        if(booking == null) {
            logPrint("Creating prepayment order on empty booking");
        }
        if(booking.avoidCreateInvoice && 
                getSession() != null && 
                getSession().currentUser != null && 
                (getSession().currentUser.isAdministrator() || getSession().currentUser.isEditor())) {
            return "";
        }
        NewOrderFilter filter = new NewOrderFilter();
        logPrint("Creating prepayment order: " + booking.id + ", userid: " + booking.userId);
        filter.prepayment = true;
        filter.endInvoiceAt = booking.getEndDate();
        filter.forceInvoicing = true;
        createOrder(booking.id, filter);
        return "";

    }

    private List<CartItem> createCartItemsForRoom(Date startDate, Date endDate, PmsBooking booking, PmsBookingRooms room, NewOrderFilter filter) {
        startDate = normalizeDate(startDate, true);
        endDate = normalizeDate(endDate, false);
        
        List<CartItem> items = new ArrayList();
        int daysInPeriode = Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
        if(booking.priceType.equals(PmsBooking.PriceType.daily)) {
            if(room.isSameDay(startDate, endDate)) {
                daysInPeriode = 1;
            }
        }
        if(booking.priceType.equals(PmsBooking.PriceType.monthly)) {
            daysInPeriode = getNumberOfMonthsBetweenDates(startDate, endDate);
            if(daysInPeriode > 1000 || pmsManager.getConfigurationSecure().hasNoEndDate) {
                //Infinate dates, noone wants to pay 100 years in advance.
                if(daysInPeriode > 1000) {
                    daysInPeriode = pmsManager.getConfigurationSecure().whenInfinteDateFirstOrderTimeUnits;
                    if(booking.periodesToCreateOrderOn != null) {
                        daysInPeriode = booking.periodesToCreateOrderOn;
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);
                    cal.add(Calendar.MONTH, daysInPeriode);
                    endDate = cal.getTime();
                    if(filter != null) {
                        filter.endInvoiceAt = endDate;
                    }
                }
            }
        }
        Double price = getOrderPriceForRoom(room, startDate, endDate, booking.priceType);

        BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
        if(type != null) {
            CartItem item = createCartItem(type.productId, type.name, room, startDate, endDate, price, daysInPeriode, "");
            if(item != null) {
                if(storeId.equals("13442b34-31e5-424c-bb23-a396b7aeb8ca")) {
                    item.getProduct().name = item.getProduct().metaData;
                    item.getProduct().metaData = "";
                    item.getProduct().additionalMetaData = "";
                }
                if(price != 0) {
                    items.add(item);
                }
            }
        }
        
        List<CartItem> addonItems = createCartItemsFromAddon(room, startDate, endDate);
        items.addAll(addonItems);
        
        return items;
    }

    private List<CartItem> createCartItemsFromAddon(PmsBookingRooms room, Date startDate, Date endDate) {
        HashMap<String, Integer> products = new HashMap();
        for(PmsBookingAddonItem addon : room.addons) {
            if(productManager.getProductUnfinalized(addon.productId) != null) {
                products.put(addon.productId, 0);
            }
        }
        
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        List<CartItem> result = new ArrayList();
        for(String productId : products.keySet()) {
            Date endDateToUse = endDate;
            if(isSameDay(endDate, room.date.end)) {
                endDateToUse = room.date.end;
            }
            List<PmsBookingAddonItem> items = room.getAllAddons(productId, startDate, endDateToUse);
            if(storeId.equals("13442b34-31e5-424c-bb23-a396b7aeb8ca")) {
                result.addAll(createAddonsCartItemsToInvoiceForGetShop(items, room));
            } else {
                for(int iteration = 0; iteration < 2; iteration++) {
                    if(items.size() > 0) {
                        Date startDateToAdd = startDate;
                        Date endDateToAdd = endDate;
                        double price = 0;
                        int count = 0;
                        int type = 0;
                        for(PmsBookingAddonItem check : items) {
                            if(iteration == 0 && !check.isIncludedInRoomPrice) {
                                //First count all that should be grouped.
                                continue;
                            }
                            if(iteration == 1 && check.isIncludedInRoomPrice) {
                                continue;
                            }
                            price += check.price * check.count;
                            count += check.count;
                            if(check.addonType != null) {
                                type = check.addonType;
                            }
                        }
                        if(type == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) {
                            endDateToAdd = startDate;
                        }
                        if(type == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
                            startDateToAdd = endDate;
                        }

                        if(count > 0) {
                            String groupId = "";
                            if(iteration == 0) {
                                groupId = room.pmsBookingRoomId;
                            }

                            endDateToAdd = startDateToAdd;
                            CartItem item = createCartItem(productId, null, room, startDateToAdd, endDateToAdd, price / count, count, groupId);
                            if(item != null) {
                                result.add(item);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private void clearCart() {
        roomIdsInCart = new ArrayList();
        if(!avoidOrderCreation) {
            List<CartItem> toRemove = new ArrayList();
            for(CartItem item : itemsToReturn) {
                if(item != null && item.addedBy != null && item.addedBy.equals("pmsquickproduct")) {
                    continue;
                }
                toRemove.add(item);
            }
            itemsToReturn.removeAll(toRemove);
        }
    }

    private void clearRealCart() {
            List<CartItem> toRemove = new ArrayList();
            for(CartItem item : cartManager.getCart().getItems()) {
                if(item.addedBy != null && item.addedBy.equals("pmsquickproduct")) {
                    continue;
                } 
                toRemove.add(item);
            }
            for(CartItem remove : toRemove) {
                cartManager.getCart().removeItem(remove.getCartItemId());
            }
    }
    
    private void updateCart() {
        for(CartItem item : itemsToReturn) {
            if(item != null) {
                item.doFinalize();
            }
        }
        clearRealCart();
        cartManager.getCart().addCartItems(itemsToReturn);
        if(pmsManager.getConfigurationSecure().autoSumarizeCartItems) {
            cartManager.summarizeItems();
        }
        checkIfOrderNeedsToBeSplitted();
    }

    private CartItem createCartItemForCart(String productId, int count, String roomId) {
        CartItem item = new CartItem();
        Product product = productManager.getProductUnfinalized(productId);
        if(product == null) {
            logPrint("Product: " + productId + " does not exists anymore");
            return null;
        }
        item.setProduct(product.clone());
        item.getProduct().externalReferenceId = roomId;
        item.setCount(count);
        item.pmsBookingId = pmsManager.getBookingFromRoom(roomId).id;
        roomIdsInCart.add(roomId);
        
        try {
            if(item.getProduct().taxGroupObject == null) {
                messageManager.sendErrorNotificationToEmail("Null tax object found", "In product: " + productId, new Exception());
            }
        }catch(Exception error) {
            
        }
        
        setDepartmentId(item, roomId);
        
        if(!runningDiffRoutine) {
            addItemToItemsToReturn(item);
        }
        return item;
    }

    private PmsBookingAddonItem getAddonConfig(String productId) {
        if(productId == null) {
            return null;
        }
        
        for(PmsBookingAddonItem addon : pmsManager.getConfigurationSecure().addonConfiguration.values()) {
            if(addon.productId != null && productId.equals(addon.productId)) {
                return addon;
            }
        }
        return null;
    }

    private HashMap<String, List<CartItem>> getAllRoomsFromExistingOrder(List<String> orderIds) {
        HashMap<String, List<CartItem>> res = new HashMap();
        for(String orderId : orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            for(CartItem item : order.cart.getItems()) {
                if(item.getProduct() != null) {
                    if(item.getProduct().externalReferenceId != null) {
                        String id = item.getProduct().externalReferenceId;
                        if(!res.containsKey(id)) {
                            res.put(id, new ArrayList());
                        }
                    }
                }
            }
        }
        return res;
    }

    private void creditRoomForPeriode(Date start, Date end, PmsBooking booking, PmsBookingRooms room) {
        List<CartItem> items = createCartItemsForRoom(start, end, booking, room, null);
        for(CartItem item : items) {
            item.setCount(item.getCount() * -1);
        }
    }

    private void checkIfNeedCrediting(PmsBookingRooms room) {
        if(!pmsManager.getConfigurationSecure().autoGenerateChangeOrders) {
            return;
        }
        PmsBooking booking = pmsManager.getBookingFromRoom(room.pmsBookingRoomId);
        if(room.invoicedFrom != null && room.invoicedFrom.before(room.date.start) && !room.isSameDay(room.invoicedFrom, room.date.start)) {
            Date invoiceStart = room.invoicedFrom;
            Date invoiceEnd = room.date.start;
            if(room.invoicedTo != null && room.date.start.after(room.invoicedTo)) {
                invoiceStart = room.invoicedFrom;
                invoiceEnd = room.invoicedTo;
            }
            creditRoomForPeriode(invoiceStart, invoiceEnd, booking, room);
        }
        if(room.invoicedTo != null && room.invoicedFrom != null && room.invoicedTo.after(room.date.end) && !room.isSameDay(room.invoicedTo, room.date.end)) {
            Date invoiceStart = room.date.end;
            Date invoiceEnd = room.invoicedTo;
            if(room.date.end.before(room.invoicedFrom)) {
                invoiceStart = room.invoicedFrom;
                invoiceEnd = room.invoicedTo;
            }
            creditRoomForPeriode(invoiceStart, invoiceEnd, booking, room);
            if(!avoidOrderCreation) {
                room.invoicedTo = room.date.end;
                saveObject(booking);
            }
        }
    }

    private Double getOrderPriceForRoom(PmsBookingRooms room, Date startDate, Date endDate, Integer priceType) {
        PmsBooking booking = pmsManager.getBookingFromRoom(room.pmsBookingRoomId);
        boolean includeTaxes = true;
        
        startDate = adjustDateForCount(startDate, priceType, true);
        endDate = adjustDateForCount(endDate, priceType, false);
        
        Double price = 0.0;
        if(pmsManager.getConfigurationSecure().getUsePriceMatrixOnOrder()) {
            Calendar calStart = Calendar.getInstance();
            updatePriceMatrix(booking, room, priceType);
            calStart.setTime(startDate);
            int count = 0;
            while(true) {
                count++;
                String offset = room.getOffsetKey(calStart, priceType);
                if(priceType == PmsBooking.PriceType.daily || priceType == PmsBooking.PriceType.progressive || priceType == PmsBooking.PriceType.interval) {
                    calStart.add(Calendar.DAY_OF_YEAR,1);
                }
                if(priceType == PmsBooking.PriceType.monthly) {
                    calStart.add(Calendar.MONTH,1);
                }
                if(room.priceMatrix.containsKey(offset)) {
                    price += room.priceMatrix.get(offset);
                }

                if(calStart.getTime().after(endDate)) {
                    break;
                }
            }
            
            Double addonsIncluded = getAddonsPriceIncludedInRoom(room, startDate, endDate);
            price = price - addonsIncluded;
            price /= count;
        } else {
            price = room.price;
        }
        
        if (pmsManager.getPriceObjectFromBooking(booking).pricesExTaxes && includeTaxes) {
            double tax = 1 + (calculateTaxes(room.bookingItemTypeId) / 100);
            //Order price needs to be inc taxes.. 
            price *= tax;
        }
        
        return price;
    }

    private Order getUnpaidOrder(PmsBooking booking) {
        for(String key : booking.orderIds) {
            Order ord = orderManager.getOrderSecure(key);
            if(ord.closed) {
                continue;
            }
            if(ord.status != Order.Status.PAYMENT_COMPLETED && !ord.transferredToAccountingSystem) {
                return ord;
            }
        }
        return null;
    }

    private List<CartItem> getChangesForBooking(String bookingId, NewOrderFilter filter) {
        runningDiffRoutine = true;
        List<CartItem> returnresult = new ArrayList();
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.invoicedFrom == null || room.invoicedTo == null) {
                continue;
            }
            
            if(filter.pmsRoomId != null && !filter.pmsRoomId.isEmpty()) {
                if(!filter.pmsRoomId.equals(room.pmsBookingRoomId)) {
                    continue;
                }
            }
            
            List<CartItem> orderRoomItems = getAllOrderItemsForRoomOnBooking(room.pmsBookingRoomId, booking.id);
            List<CartItem> roomItems = createCartItemsForRoom(room.invoicedFrom, room.invoicedTo, booking, room, filter);
            
            List<BookingOrderSummary> ordersummaries = summaries(orderRoomItems);
            List<BookingOrderSummary> roomSummary = summaries(roomItems);
            List<BookingOrderSummary> diff = diffItems(ordersummaries, roomSummary);
            for(BookingOrderSummary diffResult : diff) {
               int count = diffResult.count;
               if(count == 0) {
                   count = 1;
               }
               PmsBookingAddonItem addon = pmsManager.getConfigurationSecure().getAddonFromProductId(diffResult.productId);
               Date startInvoiceDate = room.invoicedFrom;
               Date endInvoiDate = room.invoicedTo;
               if(addon != null) {
                   if(addon.addonType == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
                       startInvoiceDate = endInvoiDate;
                   }
                   if(addon.addonType == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) {
                       endInvoiDate = startInvoiceDate;
                   }
               } 
               
               Double price = diffResult.price;
               int countToDivide = count;
                if(price.intValue() != 0) {
                    if(price < 0) {
                        price *= -1;
                        count *= -1;
                    }
                    CartItem item = createCartItem(diffResult.productId, 
                            null, 
                            room, 
                            startInvoiceDate, 
                            endInvoiDate, 
                            price / countToDivide, 
                            count, "");
               
                    if(item != null && diffResult.price != 0.0) {
                        returnresult.add(item);
                    }
                }
            }
            
        }
        
        for(PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            if(room.isDeleted() && !room.credited) {
                List<CartItem> credited = creditAllLinesOnBookingForRoom(booking.id, room.pmsBookingRoomId);
                returnresult.addAll(credited);
                if(!avoidOrderCreation) {
                    List<PmsBookingAddonItem> toRemove = new ArrayList();
                    for(PmsBookingAddonItem tmp : room.addons) {
                        PmsBookingAddonItem base = pmsManager.getBaseAddon(tmp.productId);
                        if(base != null && base.noRefundable) {
                            continue;
                        }
                        toRemove.add(tmp);
                    }
                    room.addons.removeAll(toRemove);
                    room.credited = true;
                }
                for(PmsBookingAddonItem item : room.addons) {
                    PmsBookingAddonItem base = pmsManager.getBaseAddon(item.productId);
                    if(base != null && base.noRefundable) {
                        CartItem addonToAdd = createCartItem(item.productId, item.getName(), room, room.date.start, room.date.end, item.price, item.count, "");
                        if(addonToAdd != null) {
                            returnresult.add(addonToAdd);
                        }
                    }
                }
            }
        }
        
        runningDiffRoutine = false;
        addItemsToItemToReturn(returnresult);
        return returnresult;
    }


    private List<CartItem> getAllOrderItemsForRoomOnBooking(String pmsBookingRoomId, String id) {
        List<CartItem> items = new ArrayList();
        PmsBooking booking = pmsManager.getBookingUnsecure(id);
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order == null) {
                continue;
            }
            for(CartItem item : order.cart.getItems()) {
                Product product = item.getProduct();
                if(product != null && product.externalReferenceId != null && product.externalReferenceId.equals(pmsBookingRoomId)) {
                    items.add(item);
                }
            }
        }
        return items;
    }

    @Override
    public void createPeriodeInvoice(Date start, Date end, Double amount, String roomId) {
        int days = getNumberOfDays(start, end) - 1; //Its not number of days, but number of nights.
        double price = amount / days;
        PmsBooking booking = pmsManager.getBookingFromRoom(roomId);
        boolean all = false;
        if(booking == null) {
            booking = pmsManager.getBooking(roomId);
            all = true;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        
        clearCart();
        for(PmsBookingRooms room : booking.rooms) {
            if(!all) {
                if(room.pmsBookingRoomId.equals(roomId)) {
                    continue;
                }
            }
            
            while(true) {
                String offset = PmsBookingRooms.getOffsetKey(cal, 1);
                room.priceMatrix.put(offset, price);
                cal.add(Calendar.DAY_OF_YEAR, 1);
                Date nextDay = cal.getTime();
                if(end.before(nextDay)) {
                    break;
                }
                
            }
            room.invoicedTo = start;
        }
        
        NewOrderFilter filter = new NewOrderFilter();
        filter.endInvoiceAt = end;
        if(!all) {
            filter.pmsRoomId = roomId;
        }
        filter.endInvoiceAt = end;
        filter.avoidOrderCreation = false;
        filter.createNewOrder = true;
        filter.ignoreCheckChangesInBooking = true;
        
        createOrder(booking.id, filter);
    }
    
    @Override
    public List<PmsSubscriptionOverview> getSubscriptionOverview(Date start, Date end) {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = "active";
        filter.startDate = start;
        filter.endDate = end;
        
        List<PmsSubscriptionOverview> result = new ArrayList();
        
        List<PmsRoomSimple> rooms = pmsManager.getSimpleRooms(filter);
        for(PmsRoomSimple simple : rooms) {
            List<Order> orders = getOrdersFromRoom(simple.pmsRoomId);
            Order latestOrder = getLatestOrder(orders, start, end);
            PmsSubscriptionOverview toAdd = new PmsSubscriptionOverview();
            PmsBooking booking = pmsManager.getBooking(simple.bookingId);
            PmsBookingRooms room = booking.getRoom(simple.pmsRoomId);
            
            toAdd.price = simple.price;
            toAdd.paid = false;
            toAdd.roomName = simple.room;
            toAdd.usersName = simple.owner;
            toAdd.orderValue = 0.0;
            toAdd.start = simple.start;
            toAdd.end = simple.end;
            toAdd.confirmed = booking.confirmed;
            toAdd.userId = pmsManager.getBookingUnfinalized(simple.bookingId).userId;
            toAdd.invoicedTo = room.invoicedTo;
            toAdd.email = simple.ownersEmail;
            
            User user = userManager.getUserById(toAdd.userId);
            toAdd.cardsSaved = user.savedCards.size();
            
            if(latestOrder != null) {
                toAdd.orderCreationDate = latestOrder.rowCreatedDate;
                toAdd.orderValue = getTotalValue(latestOrder, simple.pmsRoomId);
                toAdd.latestInvoiceEndDate = latestOrder.getEndDateByItems();
                toAdd.latestInvoiceStartDate = latestOrder.getStartDateByItems();
                toAdd.paid = latestOrder.status == Order.Status.PAYMENT_COMPLETED;
                toAdd.paymentType = latestOrder.payment.paymentType;
            }
            result.add(toAdd);
        }
        
        return result;
    }
    
    private Order getLatestOrder(List<Order> orders, Date start, Date end) {
        Order latest = null;
        for(Order ord : orders) {
            Date orderEnd = ord.getEndDateByItems();
            if(orderEnd != null) {
                if(latest == null || orderEnd.after(latest.getEndDateByItems())) {
                    latest = ord;
                }
            }
        }
        return latest;
    }
    
    private void checkIfOrderNeedsToBeSplitted() {
        if(!pmsManager.getConfigurationSecure().splitOrderIntoMonths) {
            return;
        }
        List<CartItem> items = cartManager.getCart().getItems();
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Gson gson = new Gson();
        
        List<CartItem> newItems = new ArrayList();
        List<CartItem> toRemove = new ArrayList();
        for(CartItem item : items) {
            if(item.startDate == null || item.endDate == null) {
                continue;
            }
            if(sameMonth(item.startDate, item.endDate) || isNotRecurring(item)) {
                continue;
            }
            toRemove.add(item);
            cal.setTime(item.startDate);
            int curMonth = cal.get(Calendar.MONTH);
            Date startOnMonth = item.startDate;
            int daysInMonth = 0;
            CartItem toAdd = null;
            
            while(true) {
                daysInMonth++;
                cal.add(Calendar.DAY_OF_YEAR, 1);
                if(cal.get(Calendar.MONTH) != curMonth) {
                    Date endInMonth = cal.getTime();
                    String copy = gson.toJson(item);
                    toAdd = gson.fromJson(copy, CartItem.class);
                    
                    cal2.setTime(startOnMonth);
                    int totalDays = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
                    double diff = ( (double)daysInMonth / (double)totalDays);
                    if(daysInMonth != totalDays) {
                        toAdd.getProduct().price = toAdd.getProduct().price * diff;
                        toAdd.getProduct().priceExTaxes = toAdd.getProduct().priceExTaxes * diff;
                    }
                    
                    toAdd.startDate = startOnMonth;
                    toAdd.endDate = endInMonth;
                    toAdd.setCount(1);
                    toAdd.refreshCartItemId();
                    
                    newItems.add(toAdd);
                    
                    startOnMonth = cal.getTime();
                    daysInMonth = 0;
                    curMonth = cal.get(Calendar.MONTH);
                }
                if(cal.getTime().after(item.endDate)) {
                    if(!pmsManager.getConfigurationSecure().orderEndsFirstInMonth) {
                        cal2.setTime(startOnMonth);
                        
                        String copy = gson.toJson(item);
                        CartItem lastAdd = gson.fromJson(copy, CartItem.class);
                        lastAdd.refreshCartItemId();
                        lastAdd.startDate = toAdd.endDate;
                        lastAdd.endDate = item.endDate;
                        lastAdd.setCount(1);
                        newItems.add(lastAdd);
                        
                        int totalDays = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
                        double diff = ( (double)daysInMonth / (double)totalDays);
                        
                        lastAdd.getProduct().price = lastAdd.getProduct().price * diff;
                        lastAdd.getProduct().priceExTaxes = lastAdd.getProduct().priceExTaxes * diff;
                        lastAdd.endDate = item.endDate;
                    }
                    break;
                }
            }
        }
        for(CartItem remove : toRemove) {
            cartManager.getCart().removeItem(remove.getCartItemId());
        }
        cartManager.getCart().addCartItems(newItems);
    }

    public void createVirtualOrder(String bookingId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        if(booking == null) {
            return;
        }
        if(!booking.isCompletedBooking()) {
            return;
        }
        
        if(userManager.getUserById(booking.userId) == null) {
            return;
        }
        
        double total = booking.getTotalPrice();
        double totalOrder = getTotalOrderPrice(booking);
        double diff = totalOrder - total;
        if (total != totalOrder) {
            itemsToReturn.clear();
            NewOrderFilter filter = new NewOrderFilter();
            filter.avoidOrderCreation = true;
            filter.endInvoiceAt = booking.getEndDate();
            
            if(filter.endInvoiceAt != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(filter.endInvoiceAt);
                cal.add(Calendar.DAY_OF_YEAR, 1);
                filter.endInvoiceAt = cal.getTime();
            }
            
            createOrder(bookingId, filter);
            if(booking != null && !supportsDailyPmsInvoiceing(booking.id)) {
                updateCart();
            }
            Order order = createOrderFromCart(booking, null, true);
            
            double newDiff = diff + orderManager.getTotalAmount(order);
            newDiff = Math.round(newDiff);
            if(newDiff != 0.0) {
//                System.out.println("Failed when creating virtual order : " + bookingId + " - " + newDiff);
//                System.out.println(pmsManager.dumpBooking(booking));
            }
        }
        
    }

    private double getTotalOrderPrice(PmsBooking booking) {
        Double total = 0.0;
        for(String orderId : booking.orderIds) {
            total += orderManager.getTotalAmount(orderManager.getOrderSecure(orderId));
        }
        return total;
    }

    @Override
    public List<String> convertCartToOrders(String id, Address address, String paymentId, String orderCreationType, Date overrideDate) {
        
        List<String> toreturn = new ArrayList();
        
        Cart cart = cartManager.getCart();
        cart.overrideDate = overrideDate;
        
        HashMap<String, List<CartItem>> groupedItems = new HashMap();
        
        if(orderCreationType != null && (orderCreationType.equals("uniqueorder") || orderCreationType.equals("uniqueordersendpaymentlink"))) {
            groupedItems = groupItemsOnOrder(cart);
        } else if(orderCreationType != null && orderCreationType.equals("merged")) {
            groupedItems = groupItemsOnBookingId(cart);
            paymentId = "cbe3bb0f-e54d-4896-8c70-e08a0d6e55ba";
        } else {
            groupedItems.put("allrooms", cart.getItems());
        }
        
        for(String roomId : groupedItems.keySet()) {
            cartManager.clear();
            cartManager.getCart().addCartItems(groupedItems.get(roomId));
            
            Order order = orderManager.createOrder(address);
            order.createByManager = "PmsDailyOrderGeneration";
            order.payment = new Payment();
            order.payment.paymentId = paymentId;

            Application paymentApplication = storeApplicationPool.getApplication(paymentId);
            order.payment.paymentType = "ns_" + paymentApplication.id.replace("-", "_") + "\\" + paymentApplication.appName;
            orderManager.saveOrder(order);
            cartManager.clear();
            toreturn.add(order.id);
        }
        
        return toreturn;
    }
    
    private HashMap<String, List<CartItem>> groupItemsOnBookingId(Cart cart) {
        HashMap<String, List<CartItem>> toReturn = new HashMap();
        for(CartItem item : cart.getItems()) {
            List<CartItem> items = toReturn.get(item.pmsBookingId);
            if(items == null) {
                items = new ArrayList();
            }
            items.add(item);
            toReturn.put(item.pmsBookingId, items);
        }
        return toReturn;
    }
    
    private HashMap<String, List<CartItem>> groupItemsOnOrder(Cart cart) {
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
    
    
    @Override
    public AccountingSystemStatisticsResult getAccountingStatistics(PmsOrderStatsFilter filter) {
        List<Order> ordersToUse = getFilterOrders(filter);
        Calendar cal = Calendar.getInstance();
        AccountingSystemStatisticsResult toReturn = new AccountingSystemStatisticsResult();
        TreeMap<String, AccountingSystemStatistics> res = new TreeMap();
        for(Order ord : ordersToUse) {
            Date postingDate = gBat10AccountingSystem.getAccountingPostingDate(ord);
            String offset = "";
            if(postingDate != null) {
                if(postingDate.before(filter.start) || postingDate.after(filter.end)) {
                    continue;
                }
                cal.setTime(postingDate);
                Integer year = cal.get(Calendar.YEAR);
                Integer day = cal.get(Calendar.DAY_OF_MONTH);
                Integer month = cal.get(Calendar.MONTH)+1;
                offset = year + "-";
                if(month >= 10) { offset += month; } else { offset += "0" + month; }
                offset += "-";
                if(day >= 10) { offset += day; } else { offset += "0" + day; }
            }
            AccountingSystemStatistics toUpdate = new AccountingSystemStatistics();
            if(res.containsKey(offset)) {
                toUpdate = res.get(offset);
            }
            toUpdate.date = postingDate;
            toUpdate.addOrder(ord);
            res.put(offset, toUpdate);
        }
        
        toReturn.dayresult = res;
        toReturn.generateProductsList();
        toReturn.generateListOfOrdersNotTransferred(ordersToUse);
        
        return toReturn;
    }
}
