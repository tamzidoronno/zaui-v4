package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.getshopaccounting.DayEntry;
import com.thundashop.core.getshopaccounting.DayIncome;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsCoverageAndIncomeReportManager  extends ManagerBase implements IPmsCoverageAndIncomeReportManager {

    private HashMap<String, BigDecimal> usersTotal = new HashMap();
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    BookingEngine BookingEngine;
    
    @Autowired
    PmsManager pmsManager;
    
    @Override
    public IncomeReportResultData getStatistics(CoverageAndIncomeReportFilter filter) {
        IncomeReportResultData toReturn = new IncomeReportResultData();
        LinkedList<IncomeReportDayEntry> res = new LinkedList();
        List<DayIncome> toinclude = orderManager.getDayIncomes(filter.start, filter.end);
        usersTotal = new HashMap();
        List<Product> products = productManager.getAllProducts();
        
        addOrderIdsForFilter(filter);
        includeLostOrders(toinclude, filter);
        
        
        for(DayIncome income : toinclude) {
            BigDecimal total = new BigDecimal(0);
            IncomeReportDayEntry toadd = new IncomeReportDayEntry();
            toadd.day = income.start;
            for(Product prod : products) {
                if(!filter.products.isEmpty()) {
                    if(!filter.products.contains(prod.id)) {
                        continue;
                    }
                }
                BigDecimal totalforproduct = getTotalForProduct(income, prod.id, filter);
                toadd.products.put(prod.id, totalforproduct);
                total = total.add(totalforproduct);
            }
            toadd.total = total.doubleValue();
            res.add(toadd);
        }
        toReturn.usersTotal = usersTotal;
        toReturn.entries = res;
        return toReturn;
    }
    
    public BigDecimal getTotalForProduct(DayIncome income, String productId, CoverageAndIncomeReportFilter filter) {
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
            addToUser(order.userId, amount);
        }
        return result.multiply(new BigDecimal(-1));
    }

    void setTotalFromNewCoverageIncomeReport(PmsStatistics result, PmsBookingFilter filter) {
        CoverageAndIncomeReportFilter cfilter = new CoverageAndIncomeReportFilter();
        cfilter.start = filter.startDate;
        cfilter.end = filter.endDate;
        cfilter.incTaxes = false;
        cfilter.channel = filter.channel;
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
        
        IncomeReportResultData data = getStatistics(cfilter);
        LinkedList<IncomeReportDayEntry> incomeresult = data.entries;
        for(IncomeReportDayEntry dayEntry : incomeresult) {
            for(StatisticsEntry entry : result.entries) {
                if (PmsBookingRooms.isSameDayStatic(entry.date, dayEntry.day)) {
                    entry.totalForcasted = entry.totalPrice;
                    entry.totalPrice = dayEntry.total;
                }
            }
        }
    }

    private void addOrderIdsForFilter(CoverageAndIncomeReportFilter filter) {
        List<PmsBooking> allbookings = pmsManager.getAllBookingsUnfinalized();
        HashMap<String,Integer> orderIdMap = new HashMap();
        for(PmsBooking booking : allbookings) {
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
}
