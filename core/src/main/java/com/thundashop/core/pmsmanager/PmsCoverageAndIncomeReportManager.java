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
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsCoverageAndIncomeReportManager  extends ManagerBase implements IPmsCoverageAndIncomeReportManager {

    @Autowired
    OrderManager orderManager;
    
    @Autowired
    ProductManager productManager;
    
    @Autowired
    BookingEngine BookingEngine;
    
    @Override
    public LinkedList<IncomeReportDayEntry> getStatistics(CoverageAndIncomeReportFilter filter) {
        LinkedList<IncomeReportDayEntry> res = new LinkedList();
        List<DayIncome> toinclude = orderManager.getDayIncomes(filter.start, filter.end);
        
        List<Product> products = productManager.getAllProducts();
        
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
        return res;
    }
    
    public BigDecimal getTotalForProduct(DayIncome income, String productId, CoverageAndIncomeReportFilter filter) {
        BigDecimal result = new BigDecimal(0);
        for(DayEntry entry : income.dayEntries) {
            if(!entry.isActualIncome || entry.isOffsetRecord) {
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
            if(item.getProduct().id.equals(productId)) {
                if(filter.incTaxes) {
                    result = result.add(entry.amount);
                } else {
                    result = result.add(entry.amountExTax);
                }
            }
        }
        return result.multiply(new BigDecimal(-1));
    }

    void setTotalFromNewCoverageIncomeReport(PmsStatistics result, PmsBookingFilter filter) {
        CoverageAndIncomeReportFilter cfilter = new CoverageAndIncomeReportFilter();
        cfilter.start = filter.startDate;
        cfilter.end = filter.endDate;
        cfilter.incTaxes = false;
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
        
        LinkedList<IncomeReportDayEntry> incomeresult = getStatistics(cfilter);
        for(IncomeReportDayEntry dayEntry : incomeresult) {
            for(StatisticsEntry entry : result.entries) {
                if (PmsBookingRooms.isSameDayStatic(entry.date, dayEntry.day)) {
                    entry.totalForcasted = entry.totalPrice;
                    entry.totalPrice = dayEntry.total;
                }
            }
        }
    }
}
