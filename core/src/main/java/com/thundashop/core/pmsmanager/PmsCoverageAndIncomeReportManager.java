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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
        
        List<DayIncome> toinclude = orderManager.getDayIncomesIgnoreConfig(dayIncomeFilter);
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
        
        addOrderIdsForFilter(filter);
        includeLostOrders(toinclude, filter);
        
        
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
        for(DayEntry entry : income.dayEntries) {
            if(!entry.isActualIncome || entry.isOffsetRecord) {
                continue;
            }
            if(!filter.orderIds.isEmpty() && !filter.orderIds.contains(entry.orderId)) {
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
        cfilter.incTaxes = false;
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
        HashMap<String,Integer> orderIdMap = new HashMap();
        for(PmsBooking booking : allbookings) {

            if(!filter.segments.isEmpty()) {
                if(!booking.segmentId.isEmpty() && !filter.segments.contains(booking.segmentId)) {
                    continue;
                }
                if(booking.segmentId.isEmpty() && !filter.segments.contains("none")) {
                    continue;
                }
            }
        
            
            if(booking.isChannel(filter.channel)) {
                for(String orderId : booking.orderIds) {
                    if(!filter.orderIds.contains(orderId)) {
                        filter.orderIds.add(orderId);
                    }
                }
            } else {
                for(String orderId : booking.orderIds) {
                    if(!filter.ignoreOrderIds.contains(orderId)) {
                        filter.ignoreOrderIds.add(orderId);
                    }
                }
            }
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
                if(!filter.orderIds.contains(order.id) && !filter.ignoreOrderIds.contains(order.id)) {
                    filter.orderIds.add(order.id);
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
        List<PmsBooking> bookings = pmsManager.getAllBookings(null);
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


}
