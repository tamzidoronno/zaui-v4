package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
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

    private boolean avoidChangeInvoicedTo;
    private boolean avoidChangingInvoicedFrom;

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
        List<Order> orders = orderManager.getOrders(null, null, null);
        List<Order> ordersToUse = new ArrayList();
        for(Order order : orders) {
            if(order.testOrder) {
                continue;
            }
            if(filter.paymentMethod != null && !filter.paymentMethod.isEmpty()) {
                if(order.payment == null) {
                    continue;
                }
                String method = filter.paymentMethod.replace("-", "_");
                if(!order.payment.paymentType.contains(method)) {
                    continue;
                }
            }
            
            if(filter.paymentStatus != null) {
                if(filter.paymentStatus == -10) {
                    if(!order.transferredToAccountingSystem) {
                        continue;
                    }
                }

                if(filter.paymentStatus > 0) {
                    if(order.status != filter.paymentStatus) {
                        continue;
                    }
                }
            }
            
            ordersToUse.add(order);
        }
        PmsOrderStatistics stats = new PmsOrderStatistics();
        stats.createStatistics(ordersToUse, filter);
        return stats;
    }

    class BookingOrderSummary {
        Integer count = 0;
        Double price = 0.0;
        String productId = "";
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
    
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof PmsUserDiscount) {
                PmsUserDiscount res = (PmsUserDiscount)dataCommon;
                discounts.put(res.userId, res);
            }
        }
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

    LinkedHashMap<String, Double> buildPriceMatrix(PmsBookingRooms room, PmsBooking booking) {
        return getPriceMatrix(room.bookingItemTypeId, room.date.start, room.date.end, booking.priceType);
    }

    private LinkedHashMap<String, Double> getPriceMatrix(String typeId, Date start, Date end, Integer priceType) {
        PmsPricing prices = pmsManager.getPriceObject();
        LinkedHashMap<String, Double> price = new LinkedHashMap();
        if (prices.defaultPriceType == 1) {
            price = calculateDailyPricing(typeId, start, end);
        }
        if (prices.defaultPriceType == 2) {
            price = calculateMonthlyPricing(typeId, start, end);
        }
        if (prices.defaultPriceType == 7) {
            price = calculateProgressivePrice(typeId, start, end, 0, priceType);
        }
        if (prices.defaultPriceType == 8) {
            price = calculateIntervalPrice(typeId, start, end);
        }
        
        
        return price;
    }

    

    public void updatePriceMatrix(PmsBooking booking, PmsBookingRooms room, Date startDate, Date endDate, Integer priceType) {
        LinkedHashMap<String, Double> priceMatrix = getPriceMatrix(room.bookingItemTypeId, startDate, endDate, priceType);
        for(String key : priceMatrix.keySet()) {
            if(!room.priceMatrix.containsKey(key) || !booking.isCompletedBooking()) {
                Double price = priceMatrix.get(key);
                price = calculateDiscountCouponPrice(booking, price);
                price = getUserPrice(room.bookingItemTypeId, price, 1);
                room.priceMatrix.put(key, price);
            }
        }
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
            
            createCartItemsForRoom(start, end, booking, room);
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
        if(room.invoicedTo == null && startDate.after(endDate)) {
            //Never invoiced, and a credit not is needed?
            return;
        }
        
        List<CartItem> items = createCartItemsForRoom(startDate,endDate, booking, room);
        
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
        itemsToReturn.clear();
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
            clearCart();
            addBookingToCart(booking, filter);
            if(!itemsToReturn.isEmpty()) {
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
                    updateCart();
                    order = createOrderFromCart(booking, filter);
                    if (order == null) {
                        return "Could not create order.";
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
                                message += otherOrder.incrementOrderId + " (" + orderManager.getTotalAmount(order) + ")<br>";
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
                }
                
                for(PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
                    if(filter.pmsRoomId != null && !filter.pmsRoomId.isEmpty()) {
                        if(!room.pmsBookingRoomId.equals(filter.pmsRoomId)) {
                            continue;
                        }
                    }
                    
                    room.invoicedFrom = room.date.start;
                    if(room.date.end.before(filter.endInvoiceAt)) {
                        room.invoicedTo = room.date.end;
                    } else {
                        room.invoicedTo = filter.endInvoiceAt;
                    }
                    
                }
                lastOrderId = order.id;
                pmsManager.saveBooking(booking);
            }
        }
        
        updateCart();
        return lastOrderId;
    }
    
    public void updateAddonsByDates(PmsBookingRooms room) {
        if(!pmsManager.getConfigurationSecure().hasAddons()) {
            return;
        }
        List<PmsBookingAddonItem> toRemove = new ArrayList();
        HashMap<Integer, Integer> addTypes = new HashMap();
        for(PmsBookingAddonItem addon : room.addons) {
            if(pmsManager.getConfigurationSecure().addonConfiguration.get(addon.addonType).isSingle) {
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
            
            if(addon.date.before(room.date.start) || addon.date.after(room.date.end) &&
                    (!room.isSameDay(addon.date, room.date.start) && !room.isSameDay(addon.date, room.date.end))) {
                toRemove.add(addon);
            }
            addTypes.put(addon.addonType, 1);
        }
        room.addons.removeAll(toRemove);
        
        java.util.Calendar startCal = java.util.Calendar.getInstance();
        startCal.setTime(room.date.start);
        while(true) {
            Date time = startCal.getTime();
            for(Integer key : addTypes.keySet()) {
                PmsBookingAddonItem config = pmsManager.getConfigurationSecure().addonConfiguration.get(key);
                if(room.hasAddon(key, startCal.getTime()) == null) {
                    PmsBookingAddonItem addon = pmsManager.createAddonToAdd(config, room, time);
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
            if(!booking.ignoreCheckChangesInBooking) {
                List<CartItem> changes = getChangesForBooking(booking.id, filter);
                items.addAll(changes);
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

    private CartItem createCartItem(String productId, String name, PmsBookingRooms room, Date startDate, Date endDate, Double price, int count) {
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
        item.startDate = startDate;
        item.endDate = endDate;
        item.periodeStart = room.date.start;
        
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
        if (!booking.payedFor) {
            return false;
        }
        return true;
    }

    private Order createOrderFromCart(PmsBooking booking, NewOrderFilter filter) {
       
        User user = userManager.getUserById(booking.userId);
        if (user == null) {
            logPrint("User does not exists: " + booking.userId + " for booking : " + booking.id);
            Exception ex = new Exception();
            logPrintException(ex);
            messageManager.sendErrorNotification("User does not exists on booking, this has to be checked and fixed.", ex);
            return null;
        }

        if(user.address == null) {
            user.address = new Address();
        }
        user.address.fullName = user.fullName;

        Order order = orderManager.createOrder(user.address);
        order.userId = booking.userId;

        Payment preferred = orderManager.getStorePreferredPayementMethod();
        Payment preferredChannel = null;
        if(!filter.fromAdministrator) {
            preferredChannel = getChannelPreferredPaymentMethod(booking);
        }
        Payment preferredUser = orderManager.getUserPrefferedPaymentMethod(order.userId);
        
        if(preferredChannel != null) {
            preferred = preferredChannel;
        }
        
        if(preferredUser != null) {
            preferred = preferredUser;
        }  
        
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

        orderManager.saveOrder(order);
        return order;
    }

    private LinkedHashMap<String, Double> calculateProgressivePrice(String typeId, Date start, Date end, int offset, Integer priceType) {
        ArrayList<ProgressivePriceAttribute> priceRange = pmsManager.getPriceObject().progressivePrices.get(typeId);
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
        HashMap<String, Double> priceMatrix = getPriceMatrix(typeId, start, end, booking.priceType);
        
        Double totalPrice = 0.0;
        int count = 0;
        for(Double tmpPrice : priceMatrix.values()) {
            totalPrice += tmpPrice;
            count++;
        }
        
        Double price = totalPrice;
        
        price = calculateDiscountCouponPrice(booking, price);
        price = getUserPrice(typeId, price, count);
        
        if(avgPrice && count != 0) {
            price /= count;
        }
        
        if(price.isNaN() || price.isInfinite()) {
            logPrint("Nan price or infinite price... this is not good");
            price = 0.0;
        }
        
        return price;
    }

    private Double calculateDiscountCouponPrice(PmsBooking booking, Double price) {
        if(booking.discountType != null && booking.discountType.equals("coupon")) {
            if(booking.couponCode != null && !booking.couponCode.isEmpty()) {
                price = cartManager.calculatePriceForCoupon(booking.couponCode, price);
            }
        }
        if(booking.discountType != null && booking.discountType.equals("partnership")) {
            if(booking.couponCode != null && !booking.couponCode.isEmpty()) {
                String[] res = booking.couponCode.split(":");
                String channel = res[0];
                Integer discount = pmsManager.getPriceObject().channelDiscount.get(channel);
                if(discount != null) {
                    price = price - (price * ((double)discount / 100));
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

    
    private LinkedHashMap<String, Double> calculateDailyPricing(String typeId, Date start, Date end) {
        HashMap<String, Double> priceRange = pmsManager.getPriceObject().dailyPrices.get(typeId);

        
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

    
    private LinkedHashMap<String, Double> calculateMonthlyPricing(String typeId, Date start, Date end) {
        HashMap<String, Double> priceRange = pmsManager.getPriceObject().dailyPrices.get(typeId);
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

    private LinkedHashMap<String, Double> calculateIntervalPrice(String typeId, Date start, Date end) {
        int totalDays = Days.daysBetween(new LocalDate(start), new LocalDate(end)).getDays();
        LinkedHashMap<String, Double> res = new LinkedHashMap();
        ArrayList<ProgressivePriceAttribute> priceRange = pmsManager.getPriceObject().progressivePrices.get(typeId);
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

    private List<CartItem> createCartItemsForRoom(Date startDate, Date endDate, PmsBooking booking, PmsBookingRooms room) {
        
        startDate = normalizeDate(startDate, true);
        endDate = normalizeDate(endDate, false);
        
        List<CartItem> items = new ArrayList();
        int daysInPeriode = Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
        if(booking.priceType.equals(PmsBooking.PriceType.monthly)) {
            daysInPeriode = getNumberOfMonthsBetweenDates(startDate, endDate);
            if(daysInPeriode > 1000) {
                //Infinate dates, noone wants to pay 100 years in advance.
                daysInPeriode = pmsManager.getConfigurationSecure().whenInfinteDateFirstOrderTimeUnits;
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.add(Calendar.MONTH, daysInPeriode);
                endDate = cal.getTime();
            }
        }
        Double price = getOrderPriceForRoom(room, startDate, endDate, booking.priceType);

        BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
        if(type != null) {
            CartItem item = createCartItem(type.productId, type.name, room, startDate, endDate, price, daysInPeriode);
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
            products.put(addon.productId, 0);
        }
        
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        List<CartItem> result = new ArrayList();
        for(String productId : products.keySet()) {
            List<PmsBookingAddonItem> items = room.getAllAddons(productId, startDate, endDate);
            if(items.size() > 0) {
                Date startDateToAdd = startDate;
                Date endDateToAdd = endDate;
                double price = 0;
                int count = 0;
                int type = 0;
                for(PmsBookingAddonItem check : items) {
                    price += check.price * check.count;
                    count += check.count;
                    type = check.addonType;
                }
                if(type == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) {
                    endDateToAdd = startDate;
                }
                if(type == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
                    startDateToAdd = endDate;
                }
                
                if(count > 0) {
                    result.add(createCartItem(productId, null, room, startDateToAdd, endDateToAdd, price / count, count));
                } else {
                    logPrint("Count 0?");
                }
            }
        }
        return result;
    }

    private void clearCart() {
        if(!avoidOrderCreation) {
            itemsToReturn.clear();
        }
    }

    private void updateCart() {
        for(CartItem item : itemsToReturn) {
            item.doFinalize();
        }
        cartManager.clear();
        cartManager.getCart().addCartItems(itemsToReturn);
    }

    private CartItem createCartItemForCart(String productId, int count, String roomId) {
        CartItem item = new CartItem();
        Product product = productManager.getProduct(productId);
        item.setProduct(product.clone());
        item.setCount(count);
        item.getProduct().externalReferenceId = roomId;
        
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
        List<CartItem> items = createCartItemsForRoom(start, end, booking, room);
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
            updatePriceMatrix(booking, room, startDate, endDate, priceType);
            calStart.setTime(startDate);
            int count = 0;
            while(true) {
                count++;
                String offset = room.getOffsetKey(calStart, priceType);
                if(priceType == PmsBooking.PriceType.daily || priceType == PmsBooking.PriceType.progressive || priceType == PmsBooking.PriceType.interval) {
                    calStart.add(Calendar.DAY_OF_YEAR,1);
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
            price /= count;
        } else {
            price = room.price; 
            price = calculateDiscountCouponPrice(booking, price);
            price = getUserPrice(room.pmsBookingRoomId, price, 1);
        }
        
        if(pmsManager.getPriceObject().privatePeopleDoNotPayTaxes) {
            User user = userManager.getUserById(booking.userId);
            if(user.company.isEmpty()) {
                includeTaxes = false;
            } else {
                Company company = userManager.getCompany(user.company.get(0));
                includeTaxes = company.vatRegisterd;
            }
        }

        if (pmsManager.getPriceObject().pricesExTaxes && includeTaxes) {
            double tax = 1 + (calculateTaxes(room.bookingItemTypeId) / 100);
            //Order price needs to be inc taxes.. 
            price *= tax;
        }
        
        
        
        logPrint(price);
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
            List<CartItem> roomItems = createCartItemsForRoom(room.invoicedFrom, room.invoicedTo, booking, room);
            
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
                            count);
               
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
                    room.addons.clear();
                    room.credited = true;
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

}
