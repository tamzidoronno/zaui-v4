package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.Coupon;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.ordermanager.data.VirtualOrder;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsInvoiceManager extends GetShopSessionBeanNamed implements IPmsInvoiceManager {

    private Double getAddonsPriceIncludedInRoom(PmsBookingRooms room, Date startDate, Date endDate) {
        double res = 0.0;
        for(PmsBookingAddonItem item : room.addons) {
            if(item.date.after(startDate) && item.date.before(endDate)) {
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
            Order order = orderManager.getOrder(orderId);
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

    

    class BookingOrderSummary {
        Integer count = 0;
        Double price = 0.0;
        String productId = "";
    }

    private boolean avoidChangeInvoicedTo;
    private boolean avoidChangingInvoicedFrom;
    private List<String> roomIdsInCart = null;

    @Override
    public void markOrderAsPaid(String bookingId, String orderId) {
        Order order = orderManager.getOrder(orderId);
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
        if(order.sentToCustomer) {
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
    public boolean isRoomPaidFor(String pmsRoomId) {
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsRoomId);
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
            if(!hasRoomItems(pmsRoomId, order)) {
                continue;
            }
            hasOrders = true;
            if(order.status == Order.Status.PAYMENT_COMPLETED) {
                continue;
            }
            payedfor = false;
        }
        
        if(!hasOrders && pmsManager.getConfigurationSecure().markBookingsWithNoOrderAsUnpaid) {
            payedfor = false;
        }
        
        return payedfor;
    }
    
    private boolean hasRoomItems(String pmsRoomId, Order order) {
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
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrder(orderId);
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
        }
        
        return allItemsToMove;
    }

    public void sendRecieptOnOrder(Order order, String bookingId) {
        User user = userManager.getUserById(order.userId);
        String usersEmail = user.emailAddress;
        if(user.emailAddressToInvoice != null && !user.emailAddressToInvoice.isEmpty()) {
            usersEmail = user.emailAddressToInvoice;
        }
        sendRecieptOrInvoice(order.id, usersEmail, bookingId);
        pmsManager.logEntry("Reciept / invoice sent to : " + usersEmail + " orderid: " + order.incrementOrderId, bookingId, null);
        
    }

    @Override
    public PmsOrderStatistics generateStatistics(PmsOrderStatsFilter filter) {
        if(filter == null) {
            return new PmsOrderStatistics(null, userManager.getAllUsersMap());
        }
        List<Order> orders = orderManager.getOrders(null, null, null);
        if(filter.includeVirtual) {
            pmsManager.createAllVirtualOrders();
            orders = orderManager.getAllOrderIncludedVirtual();
        }
        List<Order> ordersToUse = new ArrayList();
        for(Order order : orders) {
            if(order.cart.getItems().isEmpty()) {
                continue;
            }
            if(order.testOrder) {
                continue;
            }
            
            if(filter.methods.isEmpty()) {
                ordersToUse.add(order);
            }
            for(PmsOrderStatsFilter.PaymentMethods pmethod : filter.methods) {
                boolean avoid = false;
                String filterMethod = pmethod.paymentMethod;
                Integer filterStatus = pmethod.paymentStatus;
                if(filterMethod != null && !filterMethod.isEmpty()) {
                    if(order.payment == null) {
                        avoid = true;
                    }
                    String method = filterMethod.replace("-", "_");
                    if(!order.payment.paymentType.contains(method)) {
                        avoid = true;
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
                if(order.isVirtual && filter.includeVirtual) {
                    avoid = false;
                }
                
                if(!avoid && !ordersToUse.contains(order)) {
                    ordersToUse.add(order);
                }
            }
            
        }
        
        List<String> roomProducts = new ArrayList();
        for(BookingItemType type : bookingEngine.getBookingItemTypes()) {
            roomProducts.add(type.productId);
        }
        
        PmsOrderStatistics stats = new PmsOrderStatistics(roomProducts, userManager.getAllUsersMap());
        stats.createStatistics(ordersToUse, filter);
        return stats;
    }

    @Override
    public List<String> validateAllInvoiceToDates() {
        List<PmsBooking> all = pmsManager.getAllBookings(null);
        List<String> result = new ArrayList();
        for(PmsBooking booking : all) {
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
                        Order order = orderManager.getOrder(orderId);
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
                       
                       if(room.invoicedTo.after(invoicedTo)) {
                            String msg = item + " marked as invoiced to: " + new SimpleDateFormat("dd.MM.yyyy").format(room.invoicedTo) + ", but only invoiced to " + new SimpleDateFormat("dd.MM.yyyy").format(invoicedTo)  + " (" + incordertouse + ")" + ", user:" + userName;
                            result.add(msg);
                            room.invoicedTo = invoicedTo;
                            messageManager.sendErrorNotification(msg, null);
                            pmsManager.saveBooking(booking);
                       }
                    }
                }
            }
        }
        return result;
    }

    private Double addDerivedPrices(PmsBooking booking, PmsBookingRooms room, Double price) {
        if(room.bookingItemTypeId == null) {
            return price;
        }
        
        PmsPricing priceObject = pmsManager.getPriceObjectFromBooking(booking);
        double toAdd = 0.0;
        if(priceObject.derivedPrices != null && priceObject.derivedPrices.containsKey(room.bookingItemTypeId)) {
            HashMap<Integer, Double> derivedPriced = priceObject.derivedPrices.get(room.bookingItemTypeId);
            for(int i = 2;i <= room.numberOfGuests;i++) {
                if(derivedPriced.containsKey(i)) {
                    toAdd += derivedPriced.get(i);
                }
            }
        }
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
        
        price = calculateDiscounts(booking, price, typeId, count, null, null, null);
        
        
        if(avgPrice && count != 0) {
            price /= count;
        }
        
        if(price.isNaN() || price.isInfinite()) {
            logPrint("Nan price or infinite price... this is not good");
            price = 0.0;
        }
        return price;
    }

    private Double calculateDiscounts(PmsBooking booking, Double price, String bookingEngineTypeId, int count, PmsBookingRooms room, Date start, Date end) {
        if(room != null) {
            price = addDerivedPrices(booking, room, price);
        }
        price = calculateDiscountCouponPrice(booking, price, start, end, bookingEngineTypeId);
        price = getUserPrice(bookingEngineTypeId, price, count);
        
        return price;
    }

    public void clearOrdersOnBooking(PmsBooking booking) {
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrder(orderId);
            if(order.closed) {
                continue;
            }
            order.cart.clear();
            orderManager.saveOrder(order);
        }
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
            result.add(orderManager.getOrder(orderId));
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
        LinkedHashMap<String, Double> priceMatrix = getPriceMatrix(room.bookingItemTypeId, room.date.start, room.date.end, booking.priceType, booking);
        if(booking.priceType == PmsBooking.PriceType.daily) {
            for(String key : priceMatrix.keySet()) {
                room.priceMatrix.put(key, room.price);
            }
        }
    }
    
    public double updatePriceMatrix(PmsBooking booking, PmsBookingRooms room, Integer priceType) {
        LinkedHashMap<String, Double> priceMatrix = getPriceMatrix(room.bookingItemTypeId, room.date.start, room.date.end, priceType, booking);
        double total = 0.0;
        int count = 0;
        if(priceType == PmsBooking.PriceType.daily) {
            for(String key : priceMatrix.keySet()) {
                if(!room.priceMatrix.containsKey(key) || !booking.isCompletedBooking()) {
                    Double price = priceMatrix.get(key);
                    Date day = PmsBookingRooms.convertOffsetToDate(key);
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
        
        if(room.invoicedTo != null && (room.isSameDay(room.invoicedTo, endDate) || room.invoicedTo.after(endDate))) {
            return;
        }
        if(endDate.after(filter.endInvoiceAt) && filter.increaseUnits > 0) {
            return;
        }
        if(room.invoicedTo == null && startDate.after(endDate)) {
            //Never invoiced, and a credit note is needed?
            return;
        }
        
        if(pmsManager.getConfigurationSecure().ignoreRoomToEndDate && endDate.before(filter.endInvoiceAt)) {
            endDate = filter.endInvoiceAt;
        }
        if(startDate.after(filter.endInvoiceAt)) {
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
        Order currentOrder = orderManager.getOrder(orderId);
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
        
        booking.orderIds.add(creditedOrder.id);
        pmsManager.saveBooking(booking);
    }
    
    @Override
    public void clearOrder(String bookingId, String orderId) {
        if(orderId.equals("createafterstay")) {
            return;
        }
        Order currentOrder = orderManager.getOrder(orderId);
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
        if(startdate)
            cal.set(Calendar.HOUR_OF_DAY, 15);
        else
            cal.set(Calendar.HOUR_OF_DAY, 12);
        
        return cal.getTime();
    }

    @Override
    public void sendRecieptOrInvoice(String orderId, String email, String bookingId) {
        Order order = orderManager.getOrder(orderId);
        order.sentToCustomer = true;
        order.closed = true;
        orderManager.saveObject(order);
        pmsManager.setOrderIdToSend(orderId);
        pmsManager.setEmailToSendTo(email);
        if(order.status == Order.Status.PAYMENT_COMPLETED) {
            pmsManager.doNotification("sendreciept", bookingId);
        } else {
            pmsManager.doNotification("sendinvoice", bookingId);
        }
    }

    public String createOrder(String bookingId, NewOrderFilter filter) {
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
                    order = orderManager.getOrder(filter.addToOrderId);
                    if(order.closed) {
                        order = null;
                    }
                } else if(filter.createNewOrder) {
                    order = null;
                }
                if(order != null) {
                    order.cart.addCartItems(itemsToReturn);
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
                    booking.orderIds.add(order.id);
                    Double total = orderManager.getTotalAmount(order);
                    if(total < 0) {

                    List<String> emails = pmsManager.getConfigurationSecure().emailsToNotify.get("creditorder");
                        String message = "Order " + order.incrementOrderId + " has been credited from external channel, total amount: " + total + "<br><br>";
                        try {
                            User user = userManager.getUserById(order.userId);
                            message += "Name: " + user.fullName + ", " + user.emailAddress + ", " + "(" + user.prefix + ") " + user.cellPhone + "<br>";
                            message += "Other orders connected to booking:<br>";
                            for(String orderId : booking.orderIds) {
                                Order otherOrder = orderManager.getOrder(orderId);
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
                        Order baseOrder = orderManager.getOrder(filter.addToOrderId);
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
                    PmsBookingAddonItem addon = pmsManager.createAddonToAdd(config, time);
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
                
                //@TODO Make it global for all.
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
        if(price.intValue() == 0) {
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
        
        if(name != null) {
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

    
    private int getNumberOfDays(Date startDate, Date endDate) {
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
            if (cal.getTime().after(endDate)) {
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
        
        order.payment = preferred;
        order.invoiceNote = booking.invoiceNote;
        
        if(order.payment != null) {
            String type = order.payment.paymentType.toLowerCase();
            if(type.contains("expedia")) {
                order.status = Order.Status.PAYMENT_COMPLETED;
                order.captured = true;
                order.payment.captured = true;
            }
        }

        if (pmsManager.getConfigurationSecure().substractOneDayOnOrder) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.rowCreatedDate);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            order.rowCreatedDate = cal.getTime();
        }

        if (order.cart.address == null || order.cart.address.address == null || order.cart.address.address.isEmpty()) {
            if (!user.company.isEmpty()) {
                Company company = userManager.getCompany(user.company.get(0));
                order.cart.address = company.address;
                order.cart.address.fullName = company.name;
            }
        }

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

    private Double calculateDiscountCouponPrice(PmsBooking booking, Double price, Date start, Date end, String typeId) {
        if(booking.couponCode != null && !booking.couponCode.isEmpty()) {
            String couponCode = booking.couponCode;
            if(booking.discountType.equals("partnership")) {
                couponCode = "partnership:" + couponCode.substring(0, couponCode.indexOf(":"));
            }
            Coupon coupon = cartManager.getCoupon(couponCode);
            if(coupon != null) {
                if(coupon.pmsWhenAvailable != null && !coupon.pmsWhenAvailable.isEmpty() && coupon.pmsWhenAvailable.equals("REGISTERED")) {
                    start = booking.rowCreatedDate;
                    end = booking.rowCreatedDate;
                }
                
                String productId = null;
                if(typeId != null) {
                    BookingItemType type = bookingEngine.getBookingItemType(typeId);
                    if(type != null) {
                        productId = type.productId;
                    }
                }
                
                if(cartManager.couponIsValid(couponCode, start, end, productId)) {
                    price = cartManager.calculatePriceForCouponWithoutSubstract(couponCode, price);
                }
            }
        }
            
        return price;
    }

    
    private Double getUserPrice(String typeId, Double price, int count) {
        if(getSession() != null && getSession().currentUser != null) {
            PmsUserDiscount discountForUser = getDiscountsForUser(getSession().currentUser.id);
            Double discount = discountForUser.discounts.get(typeId);
            if(discount != null) {
                if(discountForUser.discountType.equals(PmsUserDiscount.PmsUserDiscountType.percentage)) {
                    price = price - (price * ((double)discount / 100));
                } else {
                    price = discount * count;
                }
            }
       }
       return price;
    }

    
    private LinkedHashMap<String, Double> calculateDailyPricing(String typeId, Date start, Date end, PmsPricing prices) {
        HashMap<String, Double> priceRange = prices.dailyPrices.get(typeId);

        
        LinkedHashMap<String, Double> result = new LinkedHashMap();
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
            
            cal.add(Calendar.DAY_OF_YEAR,1);
            if (priceRange.get(dateToUse) != null) {
                price = priceRange.get(dateToUse);
            } else {
                price = defaultPrice;
            }
            
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
                        CartItem item = createCartItem(productId, null, room, startDateToAdd, endDateToAdd, price / count, count, groupId);
                        if(item != null) {
                            result.add(item);
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
        roomIdsInCart.add(roomId);
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
            Order order = orderManager.getOrder(orderId);
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
            if(room.date.start.after(room.invoicedTo)) {
                invoiceStart = room.invoicedFrom;
                invoiceEnd = room.invoicedTo;
            }
            creditRoomForPeriode(invoiceStart, invoiceEnd, booking, room);
        }
        if(room.invoicedTo != null && room.invoicedTo.after(room.date.end) && !room.isSameDay(room.invoicedTo, room.date.end)) {
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
        if(pmsManager.getConfigurationSecure().usePriceMatrixOnOrder) {
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
                if(!room.priceMatrix.containsKey(offset)) {
                    logPrint("Huston, we have a problem: " + offset);
                } else {
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
            Order ord = orderManager.getOrder(key);
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
                        CartItem addonToAdd = createCartItem(item.productId, item.name, room, room.date.start, room.date.end, item.price, item.count, "");
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
            Order order = orderManager.getOrder(orderId);
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
            System.out.println(orderEnd);
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
            if(sameMonth(item.startDate, item.endDate)) {
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
        if(!booking.isCompletedBooking()) {
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
            
            createOrder(bookingId, filter);
            updateCart();
            Order order = createOrderFromCart(booking, null, true);
            
            double newDiff = diff + orderManager.getTotalAmount(order);
            newDiff = Math.round(newDiff);
            if(newDiff != 0.0) {
                System.out.println("Failed when creating virtual order : " + bookingId + " - " + newDiff);
                pmsManager.dumpBooking(booking);
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

}
