/*
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.accountingmanager.AccountingSystemStatistics;
import com.thundashop.core.accountingmanager.AccountingSystemStatisticsResult;
import com.thundashop.core.accountingmanager.ProductStatiticsResult;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.usermanager.data.Address;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Pms invoice system.
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPmsInvoiceManager {
    @Administrator
    public void creditOrder(String bookingId, String orderId);
    
    @Administrator
    public void clearOrder(String bookingId, String orderId);

    @Administrator
    public Double getTotalPaidOnRoomOrBooking(String pmsBookingRoomId);
    
    @Administrator
    public String sendRecieptOrInvoice(String orderId, String email, String bookingId);
    
    @Administrator
    public String createOrder(String bookingId, NewOrderFilter filter);
    
    @Administrator
    public String createOrderOnUnsettledAmount(String bookingId);
    
    @Administrator
    public PmsUserDiscount getDiscountsForUser(String userId);
    
    @Administrator
    public void saveDiscounts(PmsUserDiscount discounts);
    
    @Administrator
    public boolean supportsDailyPmsInvoiceing(String bookingId);
    
    @Administrator
    public void markOrderAsPaid(String bookingId, String orderId);
    
    @Administrator
    public Payment getPreferredPaymentMethod(String bookingId, NewOrderFilter filter);
    
    @Administrator
    public List<CartItem> removeOrderLinesOnOrdersForBooking(String id, List<String> roomIds);    
    
    @Administrator
    public PmsOrderStatistics generateStatistics(PmsOrderStatsFilter filter);
    
    @Administrator
    public void saveStatisticsFilter(PmsOrderStatsFilter filter);
    
    @Administrator
    public void deleteStatisticsFilter(String id);
    
    @Administrator
    public List<PmsOrderStatsFilter> getAllStatisticsFilters();
    
    @Administrator
    public List<String> validateAllInvoiceToDates();
    
    @Administrator
    public void createPeriodeInvoice(Date start, Date end, Double amount, String roomId);
    
    @Administrator
    public Order removeDuplicateOrderLines(Order order);
    
    public List<Long> getOrdersForRoomToPay(String pmsRoomId);
    
    @Administrator
    public PmsPaymentLinksConfiguration getPaymentLinkConfig();
    
    @Administrator
    public void savePaymentLinkConfig(PmsPaymentLinksConfiguration config);
    
    public boolean isRoomPaidFor(String pmsRoomId);
    
    public List<PmsSubscriptionOverview> getSubscriptionOverview(Date start, Date end); 
    
    public String createRegisterCardOrder(String item);

    @Administrator
    public List<Order> fetchDibsOrdersToAutoPay();
    
    @Administrator
    public PmsUserDiscount getUserDiscountByCouponCode(String couponCode);
    
    @Administrator
    public Double getTotalOnOrdersForRoom(String pmsRoomId, boolean inctaxes);
    
    @Administrator
    public void recalculateAllBookings(String password);
    
    @Administrator
    public List<CartItem> getAllUnpaidItemsForRoom(String pmsRoomId);
    
    @Administrator
    public List<String> convertCartToOrders(String id, Address address, String paymentId, String orderCreationType);
    
    @Administrator
    public void saveAdvancePriceYield(PmsAdvancePriceYield yieldPlan);
    
    @Administrator
    public HashMap<String, Double> calculatePriceMatrix(PmsBooking booking, PmsBookingRooms room);
    
    @Administrator
    public List<PmsAdvancePriceYield> getAllAdvancePriceYields();
    
    @Administrator
    public PmsAdvancePriceYield getAdvancePriceYieldPlan(String id);
    
    @Administrator
    public void deleteYieldPlan(String id);
    
    @Administrator
    public AccountingSystemStatisticsResult getAccountingStatistics(PmsOrderStatsFilter filter);
}
