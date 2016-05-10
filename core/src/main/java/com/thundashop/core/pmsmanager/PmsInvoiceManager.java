package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.ibm.icu.util.Calendar;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsInvoiceManager extends GetShopSessionBeanNamed {

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
            checkForChangesInOrders(booking);
            addBookingToCart(booking, filter);
            if(!itemsToReturn.isEmpty()) {
                if(avoidOrderCreation) {
                    continue;
                }
                
                Order order = createOrderFromCart(booking);
                if (order == null) {
                    return "Could not create order.";
                }
                booking.orderIds.add(order.id);
                booking.payedFor = false;
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
    
    
    private List<CartItem> addBookingToCart(PmsBooking booking, NewOrderFilter filter) {
        List<CartItem> items = new ArrayList();
        
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if(!room.needInvoicing(filter)) {
                continue;
            }
            
            Date startDate = room.getInvoiceStartDate();
            Date endDate = room.getInvoiceEndDate(filter, booking);

            List<CartItem> itemsToadd = createCartItemAndSetPrice(startDate,endDate, booking, room);
            

            if (pmsManager.getConfigurationSecure().substractOneDayOnOrder && !filter.onlyEnded) {
                for(CartItem item : itemsToadd) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(item.endDate);
                    cal.add(Calendar.DAY_OF_YEAR, -1);
                    item.endDate = cal.getTime();
                }
            }

            items.addAll(itemsToadd);
        }
        
        return items;
    }
    
    public double calculateTaxes(String bookingItemTypeId) {
        BookingItemType item = bookingEngine.getBookingItemType(bookingItemTypeId);
        if (item.productId != null && !item.productId.isEmpty()) {
            Product product = productManager.getProduct(item.productId);
            if (product != null && product.taxGroupObject != null) {
                return product.taxGroupObject.taxRate;
            }
            return -1.0;
        }
        return -2.0;
    }

    private CartItem createCartItem(String productId, String name, PmsBookingRooms room, Date startDate, Date endDate, double price, int count) {
        BookingItem bookingitem = null;
        if (room.bookingItemId != null) {
            bookingitem = bookingEngine.getBookingItem(room.bookingItemId);
        }

        if (productId == null) {
            System.out.println("Product not set for this booking item type");
            return null;
        }
        int numberOfDays = getNumberOfDays(room, startDate, endDate);
        if (numberOfDays == 0) {
            return null;
        }
    
        CartItem item = createCartItemForCart(productId, count, room.pmsBookingRoomId);
        item.startDate = startDate;
        item.endDate = endDate;
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
        }

        
        return item;
    }

    
    private int getNumberOfDays(PmsBookingRooms room, Date startDate, Date endDate) {
        if(startDate.after(endDate)) {
            Date tmpStart = startDate;
            startDate = endDate;
            endDate = tmpStart;
        }
        
        Calendar startDateCal = Calendar.getInstance();
        startDateCal.setTime(startDate);
        startDateCal.set(Calendar.HOUR_OF_DAY, 11);
        startDate = startDateCal.getTime();
        
        Calendar endDateCal = Calendar.getInstance();
        endDateCal.setTime(endDate);
        endDateCal.set(Calendar.HOUR_OF_DAY, 11);
        endDate = endDateCal.getTime();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(room.date.start);
        cal.set(Calendar.HOUR_OF_DAY, 11);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(room.date.end);
        endCal.set(Calendar.HOUR_OF_DAY, 12);
        
        int days = 0;
        while (true) {
            if (cal.getTime().after(startDate) && (cal.getTime().before(endDate) || cal.getTime().equals(endDate))) {
                days++;
            }

            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (cal.getTime().after(endCal.getTime())) {
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

        order.payment = new Payment();
        order.payment.paymentType = user.preferredPaymentType;
        order.userId = booking.userId;
        order.invoiceNote = booking.invoiceNote;

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


    private Double getPriceInPeriode(PmsBooking booking, PmsBookingRooms room, Date startDate, Date endDate) {
        Double price = null;
        if (booking.priceType.equals(PmsBooking.PriceType.monthly)) {
            price = room.price;
        } else if (booking.priceType.equals(PmsBooking.PriceType.progressive)) {
            int days = Days.daysBetween(new LocalDate(room.date.start), new LocalDate(startDate)).getDays();
            price = calculateProgressivePrice(room.bookingItemTypeId, startDate, endDate, days, true, booking.priceType);
        } else if (booking.priceType.equals(PmsBooking.PriceType.daily)
                || booking.priceType.equals(PmsBooking.PriceType.interval)
                || booking.priceType.equals(PmsBooking.PriceType.progressive)) {
            price = room.price;
        } else if (booking.priceType.equals(PmsBooking.PriceType.weekly)) {
            price = (room.price / 7);
        }
        
        price = cartManager.calculatePriceForCoupon(booking.couponCode, price);
        return price;
    }

    
    private Double calculateProgressivePrice(String typeId, Date start, Date end, int offset, boolean avgPrice, Integer priceType) {
        ArrayList<ProgressivePriceAttribute> priceRange = pmsManager.getPriceObject().progressivePrices.get(typeId);
        if (priceRange == null) {
            System.out.println("No progressive price found for type");
            return -0.123;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int days = offset;
        Double total = 0.0;
        while (true) {
            int daysoffset = 0;
            for (ProgressivePriceAttribute attr : priceRange) {
                daysoffset += attr.numberOfTimeSlots;
                if (daysoffset > days) {
                    total += attr.price;
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

        if (avgPrice) {
            total = total / days;
        }
        return total;
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
        PmsPricing prices = pmsManager.getPriceObject();
        double price = 0.0;
        if (prices.defaultPriceType == 1) {
            price = calculateDailyPricing(typeId, start, end, avgPrice);
        }
        if (prices.defaultPriceType == 2) {
            price = calculateMonthlyPricing(typeId);
        }
        if (prices.defaultPriceType == 7) {
            price = calculateProgressivePrice(typeId, start, end, 0, avgPrice, priceType);
        }
        if (prices.defaultPriceType == 8) {
            price = calculateIntervalPrice(typeId, start, end, avgPrice);
        }
        
        if(couponCode != null && !couponCode.isEmpty()) {
            price = cartManager.calculatePriceForCoupon(couponCode, price);
        }
        
        return price;
    }

    private Double calculateDailyPricing(String typeId, Date start, Date end, boolean avgPrice) {
        HashMap<String, Double> priceRange = pmsManager.getPriceObject().dailyPrices.get(typeId);

        if (priceRange == null) {
            return 0.0;
        }

        Double defaultPrice = priceRange.get("default");
        if (defaultPrice == null) {
            defaultPrice = 0.0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int days = 0;
        Double total = 0.0;
        while (true) {
            days++;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String dateToUse = formatter.format(cal.getTime());
            if (priceRange.get(dateToUse) != null) {
                total += priceRange.get(dateToUse);
            } else {
                total += defaultPrice;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
        }
        if (avgPrice) {
            total = total / days;
        }
        return total;
    }

    
    private Double calculateMonthlyPricing(String typeId) {
        HashMap<String, Double> priceRange = pmsManager.getPriceObject().dailyPrices.get(typeId);
        if (priceRange == null) {
            return 0.0;
        }
        Double defaultPrice = priceRange.get("default");
        if (defaultPrice == null) {
            defaultPrice = 0.0;
        }

        return defaultPrice;
    }

    private Double calculateIntervalPrice(String typeId, Date start, Date end, boolean avgprice) {
        int totalDays = Days.daysBetween(new LocalDate(start), new LocalDate(end)).getDays();
        ArrayList<ProgressivePriceAttribute> priceRange = pmsManager.getPriceObject().progressivePrices.get(typeId);
        if (priceRange == null) {
            System.out.println("No progressive price found for type");
            return -0.124;
        }
        int daysoffset = 0;
        for (ProgressivePriceAttribute attr : priceRange) {
            daysoffset += attr.numberOfTimeSlots;
            if (daysoffset >= totalDays) {
                if (avgprice) {
                    return attr.price;
                } else {
                    return attr.price;
                }
            }
        }

        //Could not find price to use.
        return -0.333;
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
        createOrder(bookingId, filter);
        return "";

    }

    private void checkForChangesInOrders(PmsBooking booking) {
        if(!pmsManager.getConfigurationSecure().autoGenerateChangeOrders) {
            return;
        }
        runningDiffRoutine = true;
        HashMap<String, List<CartItem>> itemsForAllRooms = getAllRoomsFromExistingOrder(booking.orderIds);
        
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.invoicedTo == null) {
                continue;
            }
            String roomId = room.pmsBookingRoomId;
            List<CartItem> newItems = createCartItemAndSetPrice(room.date.start, room.invoicedTo, booking, booking.getRoom(roomId));
            List<CartItem> toAdd = new ArrayList();
            if(itemsForAllRooms.containsKey(roomId)) {
                toAdd = itemsForAllRooms.get(roomId);
            }
            toAdd.addAll(newItems);
            itemsForAllRooms.put(roomId, toAdd);
        }
        
        List<CartItem> diffs = new ArrayList();
        for(String roomId : itemsForAllRooms.keySet()) {
            List<CartItem> toCheckWith = itemsForAllRooms.get(roomId);
            diffs.addAll(diffItems(toCheckWith, booking.orderIds, roomId));
        }
        itemsToReturn.addAll(diffs);
        runningDiffRoutine = false;
    }

    private List<CartItem> createCartItemAndSetPrice(Date startDate, Date endDate, PmsBooking booking, PmsBookingRooms room) {
        List<CartItem> items = new ArrayList();
        int daysInPeriode = Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
        if(booking.priceType.equals(PmsBooking.PriceType.monthly)) {
            daysInPeriode = getNumberOfMonthsBetweenDates(startDate, endDate);
            if(daysInPeriode > 1000) {
                //Infinate dates, noone wants to pay 100 years in advance.
                daysInPeriode = 1;
            }
        }
        Double price = room.price;
        
        boolean includeTaxes = true;
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

    private List<CartItem> diffItems(List<CartItem> allNewItemsOnOrder, List<String> orderIds, String roomId) {
        HashMap<String, Double> oldProductTotal = new HashMap();
        HashMap<String, Double> oldProductTotalCount = new HashMap();
        
        HashMap<String, Double> newProductTotal = new HashMap();
        HashMap<String, Double> newProductTotalCount = new HashMap();
        
        Date itemFirstStartDate = null;
        Date itemFirstEndDate = null;
        
        for(String orderId : orderIds) {
            Order order = orderManager.getOrder(orderId);
            if(beforeDiffPossible(order)) {
                return new ArrayList();
            }
            for(CartItem item : getFlatCartItems(order)) {
                if(itemFirstStartDate == null || (item.startDate != null && item.startDate.before(itemFirstStartDate))) {
                    itemFirstStartDate = item.startDate;
                }
                if(itemFirstEndDate == null || (item.startDate != null && item.endDate.after(itemFirstEndDate))) {
                    itemFirstEndDate = item.endDate;
                }
                if(item.getProduct() != null && 
                        item.getProduct().externalReferenceId != null &&
                        item.getProduct().externalReferenceId.equals(roomId)) {
                    addToMap(item, oldProductTotal, false);
                    addToMap(item, oldProductTotalCount, true);
                }
            }
        }
        
        for(CartItem item : allNewItemsOnOrder) {
            if(item.getProduct() != null && 
                    item.getProduct().externalReferenceId != null &&
                    item.getProduct().externalReferenceId.equals(roomId)) {
                addToMap(item, newProductTotalCount, true);
                addToMap(item, newProductTotal, false);
            }
        }
        
        for(String productId : oldProductTotal.keySet()) {
            if(!newProductTotal.containsKey(productId)) {
                newProductTotal.put(productId, 0.0);
                newProductTotalCount.put(productId, oldProductTotalCount.get(productId) * -1);
            }
        }
        
        System.out.println("----");
        for(String id : oldProductTotal.keySet()) {
            System.out.println("Old total: " + id + " : " + oldProductTotal.get(id));
        }
        for(String id : newProductTotal.keySet()) {
            System.out.println("New total: " + id + " : " + newProductTotal.get(id));
            System.out.println("New count: " + id + " : " + newProductTotalCount.get(id));
        }
        
        List<CartItem> result = new ArrayList();
        for(String productId : newProductTotal.keySet()) {
            double diffInPrice = newProductTotal.get(productId);
            int newcount = newProductTotalCount.get(productId).intValue();
            
            if(oldProductTotal.containsKey(productId)) {
                diffInPrice = newProductTotal.get(productId) - oldProductTotal.get(productId);
            }
            
            long res = Math.round(diffInPrice / newcount);
            System.out.println("\t Diff in price: " + diffInPrice + ", new count: " + newcount + " : Diff: " + res);
            if(res != 0) {
                PmsBooking boking = pmsManager.getBookingFromRoomIgnoreDeleted(roomId);
                if(boking == null) {
                    continue;
                }
                PmsBookingRooms room = new PmsBookingRooms();
                room = boking.getRoom(roomId);
                
                BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                CartItem itemToAdd = null;
                if(res < 0) {
                    newcount *= -1;
                    res *= -1;
                }

                String name = null;
                if(type != null) {
                    name = type.name;
                }

                PmsBookingAddonItem addonConfig = getAddonConfig(productId);
                if(addonConfig != null) {
                    name = productManager.getProduct(productId).name;
                }


                itemToAdd = createCartItem(productId, name, room, room.date.start, room.invoicedTo, res, newcount);
                if(itemToAdd != null) {
                    result.add(itemToAdd);
                }
            }
        }
        return result;
    }

    private void addToMap(CartItem item, HashMap<String, Double> map, boolean count) {
        Product product = item.getProduct();
        Double total = 0.0;
        if(map.containsKey(product.id)) {
            total = map.get(product.id);
        }
        
        if(count) {
            total += item.getCount();
        } else {
            total += (product.price * item.getCount());
        }
        
        map.put(product.id, total);
    }

    private List<CartItem> getFlatCartItems(Order order) {
        List<CartItem> items = new ArrayList();
        items.addAll(order.cart.getItems());
        for(String tmpid : order.creditOrderId) {
            Order creditedorder = orderManager.getOrder(tmpid);
            items.addAll(creditedorder.cart.getItems());
        }
        return items;
    }

    private boolean beforeDiffPossible(Order order) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(order.rowCreatedDate);
        if(cal.get(Calendar.MONTH) >= 2 && cal.get(Calendar.YEAR) >= 2016) {
            return false;
        }
        return true;
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
        System.out.println(roomId);
        CartItem item = new CartItem();
        Product product = productManager.getProduct(productId);
        item.setProduct(product.clone());
        item.setCount(count);
        item.getProduct().externalReferenceId = roomId;
        
        if(!runningDiffRoutine) {
            itemsToReturn.add(item);
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
    
}
