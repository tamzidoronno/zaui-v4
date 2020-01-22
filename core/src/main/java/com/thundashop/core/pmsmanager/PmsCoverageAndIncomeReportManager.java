package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshopaccounting.DayEntry;
import com.thundashop.core.getshopaccounting.DayIncome;
import com.thundashop.core.getshopaccounting.DayIncomeFilter;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsCoverageAndIncomeReportManager  extends ManagerBase implements IPmsCoverageAndIncomeReportManager {

    private HashMap<String, BigDecimal> usersTotal = new HashMap();
    
    private HashMap<String, PmsSegment> segments = new HashMap();
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    BookingEngine BookingEngine;
    
    @Autowired
    PmsManager pmsManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon com : data.data) {
            if(com instanceof PmsSegment) {
                segments.put(com.id, (PmsSegment)com);
            }
        }
    }
    
    @Override
    public IncomeReportResultData getStatistics(CoverageAndIncomeReportFilter filter) {
        IncomeReportResultData toReturn = new IncomeReportResultData();
        LinkedList<IncomeReportDayEntry> res = new LinkedList();
        
        DayIncomeFilter dayIncomeFilter = new DayIncomeFilter();
        dayIncomeFilter.start = filter.start;
        dayIncomeFilter.end = filter.end;
        dayIncomeFilter.excludedOldOrders = false;
        dayIncomeFilter.ignoreHourOfDay = true;
        
        
        gsTiming("Before get day income");
        List<DayIncome> toinclude = orderManager.getDayIncomesIgnoreConfig(dayIncomeFilter);
        gsTiming("After get day income");
        usersTotal = new HashMap();
        List<Product> products = productManager.getAllProducts();
        List<Product> productsOnDifferentTypes = productManager.getAllProducts();
        
        if(!filter.allProducts.isEmpty()) {
            productsOnDifferentTypes.removeIf(o -> !filter.allProducts.contains(o.id));
            products.removeIf(o -> !filter.allProducts.contains(o.id));
        }
        
        if (!filter.products.isEmpty()) {
            products.removeIf(o -> !filter.products.contains(o.id));
        } 
        
        productsOnDifferentTypes.removeIf(o -> filter.products.contains(o.id));
        
        gsTiming("bofre Included lost orders");
        addOrderIdsForFilter(filter);
        gsTiming("between Included lost orders");
        includeLostOrders(toinclude, filter);
        gsTiming("Included lost orders");
        
        for(DayIncome income : toinclude) {
            BigDecimal total = new BigDecimal(0);
            IncomeReportDayEntry toadd = new IncomeReportDayEntry();
            toadd.day = income.start;
            for(Product prod : products) {
                BigDecimal totalforproduct = getTotalForProduct(income, prod.id, filter, toadd.roomsPrice, toadd.products);
                total = total.add(totalforproduct);
            }
            
            for(Product prod : productsOnDifferentTypes) {
                getTotalForProduct(income, prod.id, filter, toadd.roomsPriceOnDifferentTypes, null);
            }
            
            toadd.total = total.doubleValue();
            res.add(toadd);
        }
        toReturn.usersTotal = usersTotal;
        toReturn.entries = res;
        return toReturn;
    }
    
    public BigDecimal getTotalForProduct(DayIncome income, String productId, CoverageAndIncomeReportFilter filter, LinkedHashMap<String, BigDecimal> toadd, LinkedHashMap<String, BigDecimal> products) {
        BigDecimal result = new BigDecimal(0);
        
        HashSet<String> orderIdsToCheck = new HashSet(filter.orderIds);
        
        for(DayEntry entry : income.dayEntries) {
            if(!entry.isActualIncome || entry.isOffsetRecord  || entry.orderId == null) {
                continue;
            }
            if(!orderIdsToCheck.isEmpty() && !orderIdsToCheck.contains(entry.orderId)) {
                continue;
            }
            Order order = orderManager.getOrderDirect(entry.orderId);
            if(filter.userIds != null && !filter.userIds.isEmpty()) {
                if(!filter.userIds.contains(order.userId)) {
                    continue;
                }
            }
            CartItem item = order.cart.getCartItem(entry.cartItemId);
            if(item == null) {
                continue;
            }
            BigDecimal amount = new BigDecimal(0);
            if(item.getProduct().id.equals(productId)) {
                if(filter.incTaxes) {
                    amount = entry.amount;
                } else {
                    amount = entry.amountExTax;
                }
            }
            result = result.add(amount);
            if(products != null) {
                addToUser(order.userId, amount);
            }
            
            BigDecimal roomPriceAmount = new BigDecimal(0);
            if(item.getProduct().externalReferenceId != null && toadd.containsKey(item.getProduct().externalReferenceId)) {
                roomPriceAmount = toadd.get(item.getProduct().externalReferenceId);
            }
            roomPriceAmount = roomPriceAmount.add(amount);
            
            toadd.put(item.getProduct().externalReferenceId, roomPriceAmount);
        }
        
        if (products != null) {
            BigDecimal res = result.multiply(new BigDecimal(-1));
            products.put(productId, res);
            return res;
        }
        
        return BigDecimal.ZERO;
    }

    void setTotalFromNewCoverageIncomeReport(PmsStatistics result, PmsBookingFilter filter) {
        CoverageAndIncomeReportFilter cfilter = new CoverageAndIncomeReportFilter();
        cfilter.start = filter.startDate;
        cfilter.end = filter.endDate;
        cfilter.incTaxes = filter.priceIncTaxes;
        cfilter.channel = filter.channel;
        cfilter.departmentIds = filter.departmentIds;
        cfilter.segments = filter.segments;
        if(filter.customers != null && !filter.customers.isEmpty()) {
            cfilter.userIds.addAll(filter.customers);
        }
        if(!filter.typeFilter.isEmpty()) {
            for(String typeId : filter.typeFilter) {
                BookingItemType type = BookingEngine.getBookingItemType(typeId);
                cfilter.products.add(type.productId);
            }
        } else {
            for(BookingItemType type : BookingEngine.getBookingItemTypes()) {
                cfilter.products.add(type.productId);
            }
        }
        
        for(BookingItemType type : BookingEngine.getBookingItemTypes()) {
            cfilter.allProducts.add(type.productId);
        }
        
        IncomeReportResultData data = getStatistics(cfilter);
        LinkedList<IncomeReportDayEntry> incomeresult = data.entries;
        for(StatisticsEntry entry : result.entries) {
           boolean found = false;
           for(IncomeReportDayEntry dayEntry : incomeresult) {
                if (PmsBookingRooms.isSameDayStatic(entry.date, dayEntry.day)) {
                    entry.totalForcasted = entry.totalPrice;
                    entry.totalPrice = dayEntry.total;
                    addRoomPrices(entry, dayEntry);
                    entry.finalize();
                    found = true;
                }
            }
           if(!found) {
                entry.totalForcasted = 0.0;
                entry.totalPrice = 0.0;
           }
        }
    }

    private void addOrderIdsForFilter(CoverageAndIncomeReportFilter filter) {
        List<PmsBooking> allbookings = pmsManager.getAllBookingsUnfinalized();
        HashMap<String, Boolean> usedOrderIds = new HashMap();
        HashMap<String, Boolean> filterOrderIds = new HashMap();
        for(PmsBooking booking : allbookings) {

            if(!filter.segments.isEmpty()) {
                if(!booking.segmentId.isEmpty() && !filter.segments.contains(booking.segmentId)) {
                    continue;
                }
                if(booking.segmentId.isEmpty() && !filter.segments.contains("none")) {
                    continue;
                }
            }
            
            List<String> orderIds = pmsManager.getAllOrderIds(booking.id);
            
            if(booking.isChannel(filter.channel)) {
                for(String orderId : orderIds) {
                    if(!filterOrderIds.containsKey(orderId) && !usedOrderIds.containsKey(orderId)) {
                        filterOrderIds.put(orderId, true);
                        filter.orderIds.add(orderId);
                    }
                }
            } else {
                for(String orderId : orderIds) {
                    if(!filter.ignoreOrderIds.contains(orderId) && !usedOrderIds.containsKey(orderId)) {
                        filter.ignoreOrderIds.add(orderId);
                    }
                }
            }
            
            for(String ordId : orderIds) {
                usedOrderIds.put(ordId, true);
            }
            
        }
        
        if(!filter.segments.isEmpty() && filter.orderIds.isEmpty()) {
            filter.orderIds.add("none");
        }
    }

    private void includeLostOrders(List<DayIncome> toinclude, CoverageAndIncomeReportFilter filter) {
        if(filter.channel != null && (!filter.channel.equals("web") || filter.channel.equals("")) && !filter.channel.isEmpty()) {
            return;
        }
        if(!filter.segments.isEmpty()) {
            return;
        }
        for(DayIncome income : toinclude) {
            for(DayEntry entry : income.dayEntries) {
                Order order = orderManager.getOrderDirect(entry.orderId);
                if(order != null) {
                    if(!filter.orderIds.contains(order.id) && !filter.ignoreOrderIds.contains(order.id)) {
                        filter.orderIds.add(order.id);
                    }
                }
            }
        }
    }

    private void addToUser(String userId, BigDecimal amount) {
        amount = amount.multiply(new BigDecimal(-1));
        BigDecimal useramount = usersTotal.get(userId);
        if(useramount == null) {
            useramount = new BigDecimal(0);
        }
        useramount = useramount.add(amount);
        usersTotal.put(userId, useramount);
    }

    private void addRoomPrices(StatisticsEntry entry, IncomeReportDayEntry dayEntry) {
        entry.roomsPrice.clear();
        for(String key : dayEntry.roomsPrice.keySet()) {
            entry.roomsPrice.put(key, dayEntry.roomsPrice.get(key).doubleValue()*-1);
            if(!entry.roomsIncluded.contains(key)) {
                entry.roomsIncluded.add(key);
                entry.roomsPriceForecasted.put(key, 0.0);
                entry.roomsNotOnSameDay.add(key);
            }
        }
        
        entry.roomsPriceOnDifferentTypes.clear();
        for(String key : dayEntry.roomsPriceOnDifferentTypes.keySet()) {
            entry.roomsPriceOnDifferentTypes.put(key, dayEntry.roomsPriceOnDifferentTypes.get(key).doubleValue()*-1);
            if(!entry.roomsIncluded.contains(key)) {
                entry.roomsIncluded.add(key);
                entry.roomsPriceForecasted.put(key, 0.0);
                entry.roomsNotOnSameDay.add(key);
            }
        }
    }

    @Override
    public List<PmsSegment> getSegments() {
        if(segments.isEmpty()) {
            PmsSegment segment = new PmsSegment();
            segment.isBusiness = true;
            segment.name = "Business";
            segment.code = "business";
            saveSegments(segment);
            
            segment = new PmsSegment();
            segment.isPrivate = true;
            segment.name = "Private";
            segment.code = "private";
            saveSegments(segment);
        }
        return new ArrayList(segments.values());
    }

    @Override
    public void saveSegments(PmsSegment segment) {
        saveObject(segment);
        segments.put(segment.id, segment);
    }

    @Override
    public void deleteSegment(String segmentId) {
        PmsSegment segment = getSegment(segmentId);
        deleteObject(segment);
        segments.remove(segmentId);
    }

    @Override
    public PmsSegment getSegment(String segmentId) {
        return segments.get(segmentId);
    }

    @Override
    public PmsSegment getSegmentForBooking(String bookingId) {
        PmsBooking booking = pmsManager.getBookingUnfinalized(bookingId);
        
        if (booking.segmentId != null && !booking.segmentId.isEmpty()) {
            PmsSegment segment = getSegment(booking.segmentId);
            if (segment != null) {
                return segment;
            }
        }
        
        for(PmsSegment segment : segments.values()) {
            if(segment.types.isEmpty()) {
                continue;
            }
            List<String> types = booking.rooms.stream().map(e->e.bookingItemTypeId).collect(Collectors.toList());
            for(String typeId : types) {
                if(segment.types.contains(typeId)) {
                    return segment;
                }
            }
        }
        
        if(booking == null) {
            logPrint("Booking not exists when getting segment for booking?");
            return null;
        }
        
        User user = userManager.getUserByIdUnfinalized(booking.userId);
        if(user == null) {
            return null;
        }
        boolean isCompany = user.companyObject != null;
        for(PmsSegment segment : segments.values()) {
            if(segment.isPrivate && !isCompany) {
                return segment;
            }
            if(segment.isBusiness && isCompany) {
                return segment;
            }
        }
        return null;
    }

    @Override
    public void recalculateSegments(String segmentId) {
        List<PmsBooking> bookings = pmsManager.getAllBookingsFlat();
        for(PmsBooking booking : bookings) {
            if(booking.segmentId != null && !booking.segmentId.isEmpty()) {
                continue;
            }
            PmsSegment segment = getSegmentForBooking(booking.id);
            if(segment != null && segment.id.equals(segmentId)) {
                booking.segmentId = segment.id;
                pmsManager.saveBooking(booking);
            }
        }
    }

    @Override
    public PmsSegment getSegmentForRoom(String roomId) {
        PmsBooking booking = pmsManager.getBookingFromRoom(roomId);
        return getSegmentForBooking(booking.id);
    }

    @Override
    public void forceUpdateSegmentsOnBooking(String bookingId, String segmentId) {
        recursiveForceSegmentsOnBooking(bookingId, segmentId, new ArrayList());
    }
    
    
    public List<String> recursiveForceSegmentsOnBooking(String bookingId, String segmentId, List<String> bookingsTested) {
        if(bookingsTested.contains(bookingId)) {
            return bookingsTested;
        }
        bookingsTested.add(bookingId);
        PmsBooking booking = pmsManager.getBooking(bookingId);
        for(String orderId : booking.orderIds) {
            Order ord = orderManager.getOrder(orderId);
            for(CartItem item : ord.getCartItems()) {
                if(item.getProduct() != null && item.getProduct().externalReferenceId != null && !item.getProduct().externalReferenceId.isEmpty()) {
                    String tmpRoomId = item.getProduct().externalReferenceId;
                    if(!booking.hasRoom(tmpRoomId)) {
                        PmsBooking toCheckBooking = pmsManager.getBookingFromRoom(tmpRoomId);
                        if(toCheckBooking != null) {
                            List<String> includeAsWell = recursiveForceSegmentsOnBooking(toCheckBooking.id, segmentId, bookingsTested);
                            bookingsTested.addAll(includeAsWell);
                        }
                    }
                }
            }
        }
        booking.segmentId = segmentId;
        pmsManager.saveBooking(booking);
        return bookingsTested;
    }

    @Override
    public PmsCustomerReport getCustomerReport(PmsCustomerReportFilter filter) {
        PmsCustomerReport result = new PmsCustomerReport();
        
        DayIncomeFilter dayIncomeFilter = new DayIncomeFilter();
        dayIncomeFilter.start = filter.start;
        dayIncomeFilter.end = filter.end;
        dayIncomeFilter.excludedOldOrders = false;
        dayIncomeFilter.ignoreHourOfDay = true;
        
        HashMap<String,Order> orders = getAllOrders();
        HashMap<String,CartItem> cartItems = mapCartItems(orders);
        
        
        gsTiming("Before get day income");
        List<DayIncome> toinclude = orderManager.getDayIncomesIgnoreConfig(dayIncomeFilter);
        
        HashMap<String, User> users = userManager.getAllUsersMap();
        HashMap<String, Integer> numberOfNigthsPerUser = new HashMap();
        HashMap<String, BigDecimal> totalValuePerUser = new HashMap();
        HashMap<String, BigDecimal> totalValueSlept = new HashMap();
        
        //HashMap<Userid, HashMap<ProductId, Amount>>
        HashMap<String, HashMap<String, BigDecimal>> totalValuePerUserPerProduct = new HashMap();
        //HashMap<Userid, HashMap<ProductId, Count>>
        HashMap<String, HashMap<String, Integer>> totalCountPerUserPerProduct = new HashMap();
        
        for(DayIncome income : toinclude) {
            for(DayEntry entry : income.dayEntries) {
                if(!entry.isActualIncome || entry.isOffsetRecord) {
                    continue;
                }
                CartItem item = cartItems.get(entry.cartItemId);
                Order ord = orders.get(entry.orderId);
                
                String userId = ord.userId;
                
                BigDecimal amount = entry.amount;
                if(!filter.includeTaxex) {
                    amount = entry.amountExTax;
                }
                Integer count = entry.count;
                amount = amount.multiply(new BigDecimal(-1));
                
                BigDecimal totalValue = totalValuePerUser.get(userId);
                BigDecimal valueSlept = totalValueSlept.get(userId);
                Integer nights = numberOfNigthsPerUser.get(userId);
                
                if(nights == null) { nights = 0; }
                if(totalValue == null) { totalValue = new BigDecimal(0); }
                if(valueSlept == null) { valueSlept = new BigDecimal(0); }
                
                if(item.priceMatrix != null && !item.priceMatrix.isEmpty()) {
                   nights++; 
                   count = 1;
                   numberOfNigthsPerUser.put(userId, nights);
                   valueSlept = valueSlept.add(amount);
                }
                
                totalValue = totalValue.add(amount);
                totalValuePerUser.put(userId, totalValue);
                totalValueSlept.put(userId, valueSlept);

                updateProductMapValue(totalValuePerUserPerProduct, userId, entry, amount);
                updateProductCountValue(totalCountPerUserPerProduct, userId, entry, count);
            }
        }
        
        for(String userId : numberOfNigthsPerUser.keySet()) {
            User usr = users.get(userId);
            
            PmsCustomerReportEntry customerEntry = new PmsCustomerReportEntry();
            customerEntry.userId = userId;
            customerEntry.total = totalValuePerUser.get(userId).doubleValue();
            customerEntry.totalSlept = totalValueSlept.get(userId).doubleValue();
            customerEntry.numberOfNights = numberOfNigthsPerUser.get(userId);
            customerEntry.productValues = totalValuePerUserPerProduct.get(userId);
            customerEntry.productCount = totalCountPerUserPerProduct.get(userId);
            if(usr != null) {
                customerEntry.fullName = usr.fullName;
            }
            result.customers.add(customerEntry);
        }
        
        result.sortCustomers();
        return result;
    }

    private void updateProductMapValue(HashMap<String, HashMap<String, BigDecimal>> totalValuePerUserPerProduct, String userId, DayEntry entry, BigDecimal amount) {
        HashMap<String, BigDecimal> userProductValueMap = totalValuePerUserPerProduct.get(userId);
        if(userProductValueMap == null) { userProductValueMap = new HashMap(); }
        BigDecimal totalProductValue = userProductValueMap.get(entry.productId);
        if(totalProductValue == null) { totalProductValue = new BigDecimal(0); }
        totalProductValue = totalProductValue.add(amount);
        userProductValueMap.put(entry.productId, totalProductValue);
        totalValuePerUserPerProduct.put(userId, userProductValueMap);
    }
    

    private void updateProductCountValue(HashMap<String, HashMap<String, Integer>> totalProductCountPerUser, String userId, DayEntry entry, Integer count) {
        if(count == null) {
            return;
        }
        HashMap<String, Integer> userProductValueMap = totalProductCountPerUser.get(userId);
        if(userProductValueMap == null) { userProductValueMap = new HashMap(); }
        Integer totalProductValue = userProductValueMap.get(entry.productId);
        if(totalProductValue == null) { totalProductValue = 0; }
        totalProductValue += count;
        userProductValueMap.put(entry.productId, totalProductValue);
        totalProductCountPerUser.put(userId, userProductValueMap);
        
    }
    
    
    private HashMap<String, Order> getAllOrders() {
        HashMap<String, Order> result = new HashMap();
        List<Order> allOrders = orderManager.getAllOrders();
        for(Order ord : allOrders) {
            result.put(ord.id, ord);
        }
        return result;
    }

    private HashMap<String, CartItem> mapCartItems(HashMap<String, Order> orders) {
        HashMap<String, CartItem> result = new HashMap();
        for(Order ord : orders.values()) {
            for(CartItem item : ord.getCartItems()) {
                result.put(item.getCartItemId(), item);
            }
        }
        return result;
    }


}
