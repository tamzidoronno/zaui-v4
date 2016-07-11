package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
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

    private List<BookingOrderSummary> summaries(List<CartItem> roomItems) {
        HashMap<String, BookingOrderSummary> res = new HashMap();
        for(CartItem item : roomItems) {
            BookingOrderSummary summary = null;
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

    LinkedHashMap<String, Double> buildPriceMatrix(PmsBookingRooms room, String couponCode, Integer priceType) {
        return getPriceMatrix(room.bookingItemTypeId, room.date.start, room.date.end, priceType);
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

    

    private void updatePriceMatrix(PmsBookingRooms room, Date startDate, Date endDate, Integer priceType) {
        LinkedHashMap<String, Double> priceMatrix = getPriceMatrix(room.bookingItemTypeId, startDate, endDate, priceType);
        for(String key : priceMatrix.keySet()) {
            if(!room.priceMatrix.containsKey(key)) {
                room.priceMatrix.put(key, priceMatrix.get(key));
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
            createCartItemsForRoom(room.date.start, room.invoicedFrom, booking, room);
        }
    }

    private void checkIfNeedAdditionalEndInvoicing(PmsBookingRooms room, NewOrderFilter filter) {
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

    }

    private void autoGenerateOrders(PmsBookingRooms room, NewOrderFilter filter) {
        if(room.needInvoicing(filter)) {
//            System.out.println("NEed to create order for rooom: " + room.invoicedTo + " - " + room.date.start + " - " + room.date.end);
            BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
            if(item != null) {
                System.out.println("Item: " + item.bookingItemName);
            }
            checkIfNeedAdditionalEndInvoicing(room, filter);
        }
    }

    private Payment getChannelPreferredPaymentMethod(PmsBooking booking) {
        String channelPaymentId = pmsManager.getConfigurationSecure().channelPaymentTypes.get(booking.channel);
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
        if(currentOrder.closed) {
            creditedOrder.status = Order.Status.PAYMENT_COMPLETED;
            creditedOrder.closed = true;
            orderManager.saveOrder(currentOrder);
        }
        
        for(CartItem item : creditedOrder.cart.getItems()) {
            String roomId = item.getProduct().externalReferenceId;
            PmsBookingRooms room = booking.getRoom(roomId);
            if(room == null) {
                continue;
            }
            
            room.invoicedTo = item.getStartingDate();
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

    class BookingOrderSummary {
        Integer count = 0;
        Double price = 0.0;
        String productId = "";
    }
    
    private boolean avoidOrderCreation = false;
    private List<CartItem> itemsToReturn = new ArrayList();
    
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
    private boolean runningDiffRoutine = false;
    
    public String createOrder(String bookingId, NewOrderFilter filter) {
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
            allbookings.add(booking);
        }
        
        
        for(PmsBooking booking : allbookings) {
            clearCart();
            addBookingToCart(booking, filter);
            if(!itemsToReturn.isEmpty()) {
                if(avoidOrderCreation) {
                    continue;
                }
                
                Order order = getUnpaidOrder(booking);
                if(order != null) {
                    order.cart.addCartItems(itemsToReturn);
                    orderManager.saveOrder(order);
                } else {
                    updateCart();
                    order = createOrderFromCart(booking);
                    if (order == null) {
                        return "Could not create order.";
                    }
                    booking.orderIds.add(order.id);
                }
                if(getSession() != null && getSession().currentUser != null && 
                        (getSession().currentUser.isEditor() || getSession().currentUser.isAdministrator())) {
                    booking.avoidAutoDelete = true;
                }
                pmsManager.saveBooking(booking);
            }
        }
        
        updateCart();
        return "";
    }
    
    private void updateAddonsByDates(PmsBookingRooms room) {
        List<PmsBookingAddonItem> toRemove = new ArrayList();
        HashMap<Integer, Integer> addTypes = new HashMap();
        for(PmsBookingAddonItem addon : room.addons) {
            if(addon.isSingle) {
                continue;
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
            List<CartItem> changes = getChangesForBooking(booking.id);
            items.addAll(changes);
        }

        for (PmsBookingRooms room : booking.getActiveRooms()) {
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

                if(!avoidOrderCreation) {
                    updateAddonsByDates(room);
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
            System.out.println("Trying to create cart item with infinite or NaN price");
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
            System.out.println("Product not set for this booking item type");
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
        if(!avoidOrderCreation) {
            room.invoicedTo = endDate;
            room.invoicedFrom = room.date.start;
            System.out.println("\t new end date: " + room.invoicedTo);
        }
        
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

    private Order createOrderFromCart(PmsBooking booking) {
       
        User user = userManager.getUserById(booking.userId);
        if (user == null) {
            return null;
        }

        user.address.fullName = user.fullName;

        Order order = orderManager.createOrder(user.address);

        Payment preferred = orderManager.getStorePreferredPayementMethod();
        Payment preferredChannel = getChannelPreferredPaymentMethod(booking);
        Payment preferredUser = orderManager.getUserPrefferedPaymentMethod(order.userId);
        
        if(preferredChannel != null) {
            preferred = preferredChannel;
        }
        
        if(preferredUser != null) {
            preferred = preferredUser;
        }  
        
        order.payment = preferred;
        order.userId = booking.userId;
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
            System.out.println("No progressive price found for type");
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
    
    public Double calculatePrice(String typeId, Date start, Date end, boolean avgPrice, String couponCode, Integer priceType) {
        HashMap<String, Double> priceMatrix = getPriceMatrix(typeId, start, end, priceType);
        
        Double totalPrice = 0.0;
        int count = 0;
        for(Double tmpPrice : priceMatrix.values()) {
            totalPrice += tmpPrice;
            count++;
        }
        
        Double price = totalPrice;
        if(couponCode != null && !couponCode.isEmpty()) {
            price = cartManager.calculatePriceForCoupon(couponCode, price);
        }
        
        if(avgPrice && count != 0) {
            price /= count;
        }
        
        if(price.isNaN() || price.isInfinite()) {
            System.out.println("Nan price or infinite price... this is not good");
            price = 0.0;
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
            System.out.println("No progressive price found for type");
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

    String createPrePaymentOrder(String bookingId) {
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        if(booking.avoidCreateInvoice && 
                getSession() != null && 
                getSession().currentUser != null && 
                (getSession().currentUser.isAdministrator() || getSession().currentUser.isEditor())) {
            return "";
        }
        NewOrderFilter filter = new NewOrderFilter();
        filter.prepayment = true;
        filter.endInvoiceAt = booking.getEndDate();
        filter.forceInvoicing = true;
        createOrder(bookingId, filter);
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
                double price = 0;
                int count = 0;
                for(PmsBookingAddonItem check : items) {
                    price += check.price * check.count;
                    count += check.count;
                }
                if(count > 0) {
                    result.add(createCartItem(productId, null, room, startDate, endDate, price / count, count));
                } else {
                    System.out.println("Count 0?");
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
        if(room.invoicedFrom != null && !room.isSameDay(room.invoicedFrom, room.date.start)) {
            creditRoomForPeriode(room.invoicedFrom, room.date.start, booking, room);
            if(!avoidOrderCreation) {
                room.invoicedFrom = room.date.start;
                saveObject(booking);
            }
        }
        if(room.invoicedTo != null && room.invoicedTo.after(room.date.end) && !room.isSameDay(room.invoicedTo, room.date.end)) {
            creditRoomForPeriode(room.date.end, room.invoicedTo, booking, room);
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
            updatePriceMatrix(room, startDate, endDate, priceType);
            calStart.setTime(startDate);
            int count = 0;
            while(true) {
                count++;
                String offset = room.getOffsetKey(calStart, priceType);
                if(priceType == PmsBooking.PriceType.daily || priceType == PmsBooking.PriceType.progressive || priceType == PmsBooking.PriceType.interval) {
                    calStart.add(Calendar.DAY_OF_YEAR,1);
                }
                if(!room.priceMatrix.containsKey(offset)) {
                    System.out.println("Huston, we have a problem: " + offset);
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
        System.out.println(price);
        return price;
    }

    private Order getUnpaidOrder(PmsBooking booking) {
        for(String key : booking.orderIds) {
            Order ord = orderManager.getOrder(key);
            if(ord.status != Order.Status.PAYMENT_COMPLETED && !ord.transferredToAccountingSystem) {
                return ord;
            }
        }
        return null;
    }

    @Override
    public List<CartItem> getChangesForBooking(String bookingId) {
        runningDiffRoutine = true;
        List<CartItem> returnresult = new ArrayList();
        PmsBooking booking = pmsManager.getBookingUnsecure(bookingId);
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.invoicedFrom == null || room.invoicedTo == null) {
                continue;
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
               Double price = diffResult.price;
                if(price.intValue() != 0) {
                    CartItem item = createCartItem(diffResult.productId, 
                            null, 
                            room, 
                            room.invoicedFrom, 
                            room.invoicedTo, 
                            price / count, 
                            count);

                    if(price < 0) {
                        price *= -1;
                    }
               
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
